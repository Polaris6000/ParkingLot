/**
 * 대시보드 주차 구역 관리 JavaScript
 * JSP에서 전달받은 parkingSpotsData 사용
 */

document.addEventListener('DOMContentLoaded', function() {
    // 주차 구역 렌더링
    renderParkingSpots();

    // 검색 기능 설정
    setupSearch();
});

/**
 * 주차 구역을 화면에 렌더링
 */
function renderParkingSpots() {
    const row1 = document.getElementById('row1');
    const row2 = document.getElementById('row2');

    // 기존 내용 초기화
    row1.innerHTML = '';
    row2.innerHTML = '';

    // 데이터가 없으면 기본 20개 빈 구역 생성
    if (typeof parkingSpotsData === 'undefined' || parkingSpotsData.length === 0) {
        console.warn('주차 구역 데이터가 없습니다.');
        createEmptySpots(row1, 1, 10);
        createEmptySpots(row2, 11, 20);
        return;
    }

    // 1번 구역 (A01 ~ A10)
    parkingSpotsData.slice(0, 10).forEach(spot => {
        row1.appendChild(createSpotElement(spot));
    });

    // 2번 구역 (A11 ~ A20)
    parkingSpotsData.slice(10, 20).forEach(spot => {
        row2.appendChild(createSpotElement(spot));
    });

    // 검색 결과 강조
    if (typeof searchResultSpot !== 'undefined' && searchResultSpot) {
        highlightSearchResult(searchResultSpot);
    }
}

/**
 * 주차 구역 HTML 요소 생성
 */
function createSpotElement(spot) {
    const spotDiv = document.createElement('div');
    spotDiv.className = 'spot';
    spotDiv.dataset.spot = spot.spotNumber;
    spotDiv.dataset.carId = spot.carId || '';

    // 점유 상태에 따라 클래스 추가
    if (spot.occupied) {
        spotDiv.classList.add('occupied');
    }

    // 툴팁 설정
    spotDiv.title = spot.occupied ? spot.plateNumber : '사용 가능';

    // HTML 내용 구성
    spotDiv.innerHTML = `
        <i class="fa-solid fa-car"></i>
        <div style="font-size: 10px; margin-top: 5px;">${spot.spotNumber}</div>
        ${spot.occupied ? `<div style="font-size: 9px; color: #666;">${spot.plateNumber}</div>` : ''}
    `;

    // 클릭 이벤트
    spotDiv.addEventListener('click', function() {
        handleSpotClick(spot);
    });

    return spotDiv;
}

/**
 * 빈 주차 구역 생성 (데이터가 없을 때)
 */
function createEmptySpots(container, start, end) {
    for (let i = start; i <= end; i++) {
        // A01, A02 형식 (하이픈 없음)
        const spotNumber = `A${String(i).padStart(2, '0')}`;
        const emptySpot = {
            spotNumber: spotNumber,
            occupied: false,
            plateNumber: null,
            entryTime: null,
            carId: null
        };
        container.appendChild(createSpotElement(emptySpot));
    }
}

/**
 * 주차 구역 클릭 처리
 */
function handleSpotClick(spot) {
    if (spot.occupied && spot.carId) {
        // 점유된 구역: 상세 정보 표시 또는 페이지 이동
        console.log('차량 상세 보기:', spot.carId, spot.spotNumber);
        // TODO: 우측 패널에 상세 정보 표시 또는 페이지 이동
        // showCarDetail(spot.carId);
        // 또는
        // location.href = '/car/detail?id=' + spot.carId;
    } else {
        // 빈 구역: 입차 페이지로 이동
        console.log('입차 처리:', spot.spotNumber);
        // TODO: 입차 페이지로 이동
        // location.href = '/car/entry?spot=' + spot.spotNumber;
    }
}

/**
 * 검색 기능 설정
 */
function setupSearch() {
    const searchInput = document.getElementById('searchInput');

    if (!searchInput) return;

    // 엔터키로 검색
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            performSearch(this.value.trim());
        }
    });

    // 실시간 검색 (선택사항 - 4자리 이상 입력시)
    searchInput.addEventListener('input', function() {
        const keyword = this.value.trim();
        if (keyword.length >= 4) {
            // 실시간 검색은 주석 처리 (필요시 활성화)
            // performSearch(keyword);
        }
    });
}

/**
 * 검색 실행
 */
function performSearch(keyword) {
    if (!keyword) {
        alert('검색할 차량번호를 입력하세요.');
        return;
    }

    // 서버로 검색 요청 (POST)
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = window.location.pathname;

    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'searchKeyword';
    input.value = keyword;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

/**
 * 검색 결과 강조
 */
function highlightSearchResult(spotNumber) {
    const spots = document.querySelectorAll('.spot');
    spots.forEach(spot => {
        if (spot.dataset.spot === spotNumber) {
            spot.classList.add('search-hit');
            // 해당 구역으로 스크롤
            spot.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    });
}

/**
 * 주차 구역 상태 업데이트 (Ajax)
 */
function updateParkingSpots() {
    fetch(window.location.pathname + '?ajax=true')
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답 오류');
            }
            return response.json();
        })
        .then(data => {
            // 서버에서 받아온 데이터로 전역 변수 갱신
            parkingSpotsData = data.parkingSpots;

            // 통계 정보 업데이트
            const occupancyElem = document.getElementById('occupancy-rate');
            if (occupancyElem) {
                occupancyElem.innerText = `${data.stats.currentParkedCount} / ${data.stats.totalSpots}`;
            }

            const visitorElem = document.querySelector('.card.full .value');
            if (visitorElem) {
                visitorElem.innerText = data.stats.todayVisitorCount;
            }

            // 화면 다시 그리기
            renderParkingSpots();
        })
        .catch(error => {
            console.error('주차 현황 업데이트 실패:', error);
        });
}

/**
 * 주기적 업데이트 (선택사항)
 * 실시간 업데이트가 필요한 경우 주석 해제
 */
// setInterval(updateParkingSpots, 30000); // 30초마다 업데이트