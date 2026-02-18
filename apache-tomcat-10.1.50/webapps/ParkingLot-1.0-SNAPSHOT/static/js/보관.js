/**
 * 주차장 대시보드 클라이언트 스크립트
 *
 * 기능:
 * - 페이지 로딩 시 대시보드 데이터 자동 로딩
 * - 실시간 데이터 갱신 (자동 새로고침)
 * - 차량 검색 기능
 * - 주차 구역 시각화 및 인터랙션
 *
 * API 엔드포인트:
 * - /api/dashboard/all      : 전체 데이터 조회
 * - /api/dashboard/stats    : 통계만 조회
 * - /api/dashboard/spots    : 주차 구역만 조회
 * - /api/dashboard/search   : 차량 검색
 *
 * @author 팀 프로젝트
 * @version 1.0
 */

// ========== 전역 변수 ==========

/**
 * 컨텍스트 경로
 * JSP에서 ${pageContext.request.contextPath}로 설정됨
 */
let contextPath = '';

/**
 * 자동 새로고침 타이머 ID
 */
let autoRefreshTimer = null;

/**
 * 현재 검색된 주차 구역 번호
 */
let currentSearchSpot = null;

// ========== 페이지 초기화 ==========

/**
 * 페이지 로드 시 실행
 * - 초기 데이터 로딩
 * - 이벤트 리스너 등록
 * - 자동 새로고침 시작
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('대시보드 초기화 시작');

    // 컨텍스트 경로 설정 (JSP에서 주입)
    contextPath = document.getElementById('contextPath')?.value || '';

    // 초기 데이터 로딩
    loadDashboardData();

    // 이벤트 리스너 등록
    initEventListeners();

    // 자동 새로고침 시작 (30초마다)
    startAutoRefresh(30000);

    console.log('대시보드 초기화 완료');
});

/**
 * 이벤트 리스너 초기화
 */
function initEventListeners() {
    // 검색 버튼 클릭
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', handleSearch);
    }

    // 검색창 엔터키
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });
    }

    // 새로고침 버튼
    const refreshBtn = document.getElementById('refreshBtn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', function() {
            loadDashboardData();
            showNotification('데이터를 새로고침했습니다.');
        });
    }

    // 검색 초기화 버튼
    const clearSearchBtn = document.getElementById('clearSearchBtn');
    if (clearSearchBtn) {
        clearSearchBtn.addEventListener('click', clearSearch);
    }
}

// ========== 데이터 로딩 ==========

/**
 * 대시보드 전체 데이터 로딩
 * - 통계 정보와 주차 구역 정보를 한 번에 가져옴
 */
function loadDashboardData() {
    console.log('대시보드 데이터 로딩 시작');

    // 로딩 인디케이터 표시
    showLoadingIndicator();

    // Ajax 요청
    fetch(contextPath + '/api/dashboard/all', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            // HTTP 응답 상태 확인
            if (!response.ok) {
                throw new Error('서버 응답 오류: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('데이터 수신 성공:', data);

            // 성공 응답 확인
            if (data.success) {
                // 통계 정보 업데이트
                updateStatsDisplay(data.stats);

                // 주차 구역 시각화
                renderParkingSpots(data.parkingSpots);

            } else {
                throw new Error(data.error || '데이터 로딩 실패');
            }
        })
        .catch(error => {
            console.error('데이터 로딩 오류:', error);
            showErrorNotification('데이터를 불러오는데 실패했습니다: ' + error.message);
        })
        .finally(() => {
            // 로딩 인디케이터 숨김
            hideLoadingIndicator();
        });
}

/**
 * 통계 정보만 업데이트
 * (주차 구역 정보는 변경하지 않음)
 */
function loadStatsOnly() {
    console.log('통계 정보만 업데이트');

    fetch(contextPath + '/api/dashboard/stats', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateStatsDisplay(data.data);
            }
        })
        .catch(error => {
            console.error('통계 로딩 오류:', error);
        });
}

// ========== 통계 정보 표시 ==========

/**
 * 통계 정보 화면 업데이트
 *
 * @param {Object} stats - 통계 데이터 객체
 * @param {number} stats.currentParkedCount - 현재 주차 대수
 * @param {number} stats.totalSpots - 전체 주차 면수
 * @param {number} stats.todayVisitorCount - 금일 방문자 수
 * @param {number} stats.availableSpots - 주차 가능 대수
 */
function updateStatsDisplay(stats) {
    console.log('통계 정보 업데이트:', stats);

    // 현재 주차 대수
    const currentParkedEl = document.getElementById('currentParkedCount');
    if (currentParkedEl) {
        currentParkedEl.textContent = stats.currentParkedCount || 0;
    }

    // 주차 가능 대수
    const availableSpotsEl = document.getElementById('availableSpots');
    if (availableSpotsEl) {
        availableSpotsEl.textContent = stats.availableSpots || 0;
    }

    // 금일 방문자 수
    const todayVisitorEl = document.getElementById('todayVisitorCount');
    if (todayVisitorEl) {
        todayVisitorEl.textContent = stats.todayVisitorCount || 0;
    }

    // 점유율 계산 및 표시
    const occupancyRate = stats.totalSpots > 0
        ? Math.round((stats.currentParkedCount / stats.totalSpots) * 100)
        : 0;

    const occupancyRateEl = document.getElementById('occupancyRate');
    if (occupancyRateEl) {
        occupancyRateEl.textContent = occupancyRate + '%';
    }

    // 점유율에 따른 색상 변경
    updateOccupancyColor(occupancyRate);
}

/**
 * 점유율에 따라 색상 변경
 * - 80% 이상: 빨간색 (혼잡)
 * - 50% ~ 79%: 노란색 (보통)
 * - 50% 미만: 파란색 (여유)
 *
 * @param {number} rate - 점유율 (0-100)
 */
function updateOccupancyColor(rate) {
    const occupancyCard = document.getElementById('occupancyCard');
    if (!occupancyCard) return;

    // 기존 색상 클래스 제거
    occupancyCard.classList.remove('occupancy-low', 'occupancy-medium', 'occupancy-high');

    // 점유율에 따라 클래스 추가
    if (rate >= 80) {
        occupancyCard.classList.add('occupancy-high');
    } else if (rate >= 50) {
        occupancyCard.classList.add('occupancy-medium');
    } else {
        occupancyCard.classList.add('occupancy-low');
    }
}

// ========== 주차 구역 시각화 ==========

/**
 * 주차 구역 시각화
 * - 20개 주차 구역(A01~A20)을 2개 행으로 표시
 * - 각 구역의 상태에 따라 다른 스타일 적용
 *
 * @param {Array} parkingSpots - 주차 구역 배열
 */
function renderParkingSpots(parkingSpots) {
    console.log('주차 구역 렌더링:', parkingSpots.length + '개');

    // A구역 (A01~A10)
    const rowA = document.getElementById('parkingRowA');
    if (rowA) {
        rowA.innerHTML = renderParkingRow(parkingSpots.slice(0, 10));
    }

    // B구역 (A11~A20) - 이름은 A구역이지만 두 번째 행
    const rowB = document.getElementById('parkingRowB');
    if (rowB) {
        rowB.innerHTML = renderParkingRow(parkingSpots.slice(10, 20));
    }
}

/**
 * 주차 구역 행 HTML 생성
 *
 * @param {Array} spots - 해당 행의 주차 구역 배열 (10개)
 * @returns {string} HTML 문자열
 */
function renderParkingRow(spots) {
    return spots.map(spot => {
        // 빈 자리 / 점유 상태 결정
        const isOccupied = spot.occupied;
        const spotClass = isOccupied ? 'spot occupied' : 'spot';

        // 검색 결과 강조
        const isSearchResult = currentSearchSpot === spot.spotNumber;
        const finalClass = isSearchResult ? spotClass + ' search-hit' : spotClass;

        // 툴팁 텍스트 생성
        let tooltipText = spot.spotNumber;
        if (isOccupied) {
            tooltipText += '\n차량번호: ' + (spot.plateNumber || '정보없음');
            tooltipText += '\n입차시간: ' + (spot.entryTime || '정보없음');
        } else {
            tooltipText += '\n(사용 가능)';
        }

        // HTML 생성
        return `
            <div class="${finalClass}" 
                 data-spot="${spot.spotNumber}"
                 data-occupied="${isOccupied}"
                 data-plate="${spot.plateNumber || ''}"
                 onclick="handleSpotClick('${spot.spotNumber}')"
                 title="${tooltipText}">
                <i class="fas fa-car"></i>
                <div class="spot-label">${spot.spotNumber}</div>
            </div>
        `;
    }).join('');
}

/**
 * 주차 구역 클릭 이벤트 처리
 *
 * @param {string} spotNumber - 클릭된 주차 구역 번호 (예: 'A01')
 */
function handleSpotClick(spotNumber) {
    console.log('주차 구역 클릭:', spotNumber);

    // 상세 정보 패널 표시
    showSpotDetails(spotNumber);
}

/**
 * 주차 구역 상세 정보 표시
 * (우측 패널에 표시)
 *
 * @param {string} spotNumber - 주차 구역 번호
 */
function showSpotDetails(spotNumber) {
    const detailPanel = document.getElementById('detailPanel');
    if (!detailPanel) return;

    // 해당 주차 구역 데이터 찾기
    const spotElement = document.querySelector(`[data-spot="${spotNumber}"]`);
    if (!spotElement) return;

    const isOccupied = spotElement.dataset.occupied === 'true';
    const plateNumber = spotElement.dataset.plate;

    if (isOccupied) {
        // 점유 중인 경우: 차량 정보 표시
        detailPanel.innerHTML = `
            <h3>주차 구역: ${spotNumber}</h3>
            <div class="detail-content">
                <p><strong>상태:</strong> <span class="status-occupied">사용 중</span></p>
                <p><strong>차량번호:</strong> ${plateNumber || '정보없음'}</p>
                <hr>
                <button class="btn-primary" onclick="navigateToExit('${plateNumber}')">
                    <i class="fas fa-sign-out-alt"></i> 출차 처리
                </button>
            </div>
        `;
    } else {
        // 빈 자리인 경우: 입차 안내
        detailPanel.innerHTML = `
            <h3>주차 구역: ${spotNumber}</h3>
            <div class="detail-content">
                <p><strong>상태:</strong> <span class="status-available">사용 가능</span></p>
                <hr>
                <button class="btn-primary" onclick="navigateToEntry('${spotNumber}')">
                    <i class="fas fa-car"></i> 입차 등록
                </button>
            </div>
        `;
    }
}

// ========== 검색 기능 ==========

/**
 * 차량 번호 검색 처리
 */
function handleSearch() {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput?.value.trim();

    if (!keyword) {
        showErrorNotification('검색어를 입력해주세요.');
        return;
    }

    console.log('차량 검색:', keyword);

    // 로딩 표시
    showLoadingIndicator();

    // API 호출
    fetch(contextPath + `/api/dashboard/search?keyword=${encodeURIComponent(keyword)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log('검색 결과:', data);

            if (data.success) {
                // 검색 성공: 해당 주차 구역 강조
                currentSearchSpot = data.data.spotNumber;

                // 주차 구역 다시 렌더링 (강조 표시 적용)
                loadDashboardData();

                // 검색된 구역으로 스크롤
                scrollToSpot(data.data.spotNumber);

                // 상세 정보 표시
                showSpotDetails(data.data.spotNumber);

                showNotification(`차량을 찾았습니다: ${data.data.spotNumber}`);

            } else {
                // 검색 실패
                showErrorNotification(data.message || '차량을 찾을 수 없습니다.');
                currentSearchSpot = null;
            }
        })
        .catch(error => {
            console.error('검색 오류:', error);
            showErrorNotification('검색 중 오류가 발생했습니다.');
        })
        .finally(() => {
            hideLoadingIndicator();
        });
}

/**
 * 검색 초기화
 */
function clearSearch() {
    // 검색어 삭제
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }

    // 강조 표시 제거
    currentSearchSpot = null;

    // 데이터 다시 로딩
    loadDashboardData();

    showNotification('검색이 초기화되었습니다.');
}

/**
 * 특정 주차 구역으로 스크롤
 *
 * @param {string} spotNumber - 주차 구역 번호
 */
function scrollToSpot(spotNumber) {
    const spotElement = document.querySelector(`[data-spot="${spotNumber}"]`);
    if (spotElement) {
        spotElement.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
    }
}

// ========== 자동 새로고침 ==========

/**
 * 자동 새로고침 시작
 *
 * @param {number} interval - 새로고침 간격 (밀리초)
 */
function startAutoRefresh(interval) {
    console.log(`자동 새로고침 시작: ${interval/1000}초 간격`);

    // 기존 타이머가 있으면 중지
    stopAutoRefresh();

    // 새 타이머 시작
    autoRefreshTimer = setInterval(() => {
        console.log('자동 새로고침 실행');
        loadDashboardData();
    }, interval);
}

/**
 * 자동 새로고침 중지
 */
function stopAutoRefresh() {
    if (autoRefreshTimer) {
        clearInterval(autoRefreshTimer);
        autoRefreshTimer = null;
        console.log('자동 새로고침 중지');
    }
}

// ========== 페이지 이동 ==========

/**
 * 입차 등록 페이지로 이동
 *
 * @param {string} spotNumber - 선택된 주차 구역 번호
 */
function navigateToEntry(spotNumber) {
    window.location.href = contextPath + `/entry?spot=${spotNumber}`;
}

/**
 * 출차 처리 페이지로 이동
 *
 * @param {string} plateNumber - 차량 번호
 */
function navigateToExit(plateNumber) {
    window.location.href = contextPath + `/exit?plate=${plateNumber}`;
}

// ========== UI 헬퍼 함수 ==========

/**
 * 로딩 인디케이터 표시
 */
function showLoadingIndicator() {
    const indicator = document.getElementById('loadingIndicator');
    if (indicator) {
        indicator.style.display = 'block';
    }
}

/**
 * 로딩 인디케이터 숨김
 */
function hideLoadingIndicator() {
    const indicator = document.getElementById('loadingIndicator');
    if (indicator) {
        indicator.style.display = 'none';
    }
}

/**
 * 성공 알림 표시
 *
 * @param {string} message - 알림 메시지
 */
function showNotification(message) {
    // 간단한 알림 (실제로는 Toast UI 라이브러리 사용 권장)
    console.log('[알림]', message);

    // TODO: Toast 알림 구현
    alert(message);
}

/**
 * 에러 알림 표시
 *
 * @param {string} message - 에러 메시지
 */
function showErrorNotification(message) {
    console.error('[에러]', message);

    // TODO: 에러 Toast 구현
    alert('오류: ' + message);
}