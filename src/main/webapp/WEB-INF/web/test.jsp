<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>테스트 데이터 관리</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">
</head>
<body>
<div class="container">


    <header class="dashboard-header">
        <%@include file="common/header.jsp" %>
    </header>

    <!-- 알림 메시지 -->
    <c:if test="${not empty message}">
        <div class="alert alert-success">
            <i class="fa-solid fa-circle-check"></i> ${message}
        </div>
    </c:if>


    <div class="stats-box">
        <div class="stats-header">
        <span class="stats-title">
            <i class="fa-solid fa-chart-simple"></i> 현황
        </span>
            <button onclick="refreshStatistics()" class="btn-refresh">
                <i class="fa-solid fa-rotate"></i> 새로고침
            </button>
        </div>
        <div class="stats-content">${statistics}</div>
    </div>

    <main class="main-control-panel">

        <!-- 기능 그리드 -->
        <div class="actions-grid">

            <!-- 입차 데이터 생성 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-car"></i>
                    <h2>대량 입차</h2>
                </div>
                <form class="ajax-form" data-action="bulkEntry">
                    <input type="hidden" name="action" value="bulkEntry">
                    <div class="input-group">
                        <input type="number" name="count" value="10" min="1" max="20" required>
                        <span>대</span>
                    </div>
                    <button type="submit" class="btn btn-primary">생성</button>
                    <p class="hint">랜덤 차량번호, 주차 위치, 할인 정보 자동 생성</p>
                </form>
            </div>

            <!-- 출차 처리 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-right-from-bracket"></i>
                    <h2>대량 출차</h2>
                </div>
                <form class="ajax-form" data-action="bulkExit">
                    <input type="hidden" name="action" value="bulkExit">
                    <div class="input-group">
                        <input type="number" name="count" value="5" min="1" max="20" required>
                        <span>대</span>
                    </div>
                    <button type="submit" class="btn btn-success">처리</button>
                    <p class="hint">현재 주차중인 차량 중 랜덤 출차</p>
                </form>
            </div>

            <!-- 월정액 회원 등록 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-user-plus"></i>
                    <h2>월정액 회원</h2>
                </div>
                <form class="ajax-form" data-action="bulkMonthly">
                    <input type="hidden" name="action" value="bulkMonthly">
                    <div class="input-group">
                        <input type="number" name="count" value="10" min="1" max="100" required>
                        <span>명</span>
                    </div>
                    <button type="submit" class="btn btn-primary">등록</button>
                    <p class="hint">랜덤 차량번호, 이름, 연락처 생성 (1개월)</p>
                </form>
            </div>

            <!-- 회원 삭제 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-user-minus"></i>
                    <h2>회원 삭제</h2>
                </div>
                <form class="ajax-form" data-action="randomDeleteMonthly">
                    <input type="hidden" name="action" value="randomDeleteMonthly">
                    <div class="input-group">
                        <input type="number" name="count" value="5" min="1" max="100" required>
                        <span>명</span>
                    </div>
                    <button type="submit" class="btn btn-warning">삭제</button>
                    <p class="hint">월정액 회원 랜덤 삭제</p>
                </form>
            </div>

            <!-- 요금 정책 등록 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-coins"></i>
                    <h2>요금 정책</h2>
                </div>
                <form class="ajax-form" data-action="bulkFeePolicy">
                    <input type="hidden" name="action" value="bulkFeePolicy">
                    <div class="input-group">
                        <input type="number" name="count" value="5" min="1" max="50" required>
                        <span>개</span>
                    </div>
                    <button type="submit" class="btn btn-primary">등록</button>
                    <p class="hint">랜덤 요금 정책 생성 (히스토리 테스트)</p>
                </form>
            </div>

            <!-- 정책 삭제 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-trash-can"></i>
                    <h2>정책 삭제</h2>
                </div>
                <form class="ajax-form" data-action="randomDeletePolicy">
                    <input type="hidden" name="action" value="randomDeletePolicy">
                    <div class="input-group">
                        <input type="number" name="count" value="3" min="1" max="50" required>
                        <span>개</span>
                    </div>
                    <button type="submit" class="btn btn-warning">삭제</button>
                    <p class="hint">요금 정책 랜덤 삭제</p>
                </form>
            </div>

            <!-- 요금 정책 수동 생성 -->
            <div class="action-card">
                <div class="card-header">
                    <i class="fa-solid fa-coins" style="color: #dda20a"></i>
                    <h2>정책 수동 생성</h2>
                </div>
                <form class="ajax-form" data-action="randomDeletePolicy">
                    <div class="input-group">
                        <span>요금 정책 추가 · 수정 · 삭제</span>
                    </div>
                    <button type="submit" class="btn btn-info">
                        <a href="/setting" style="color: white">
                            페이지 이동
                        </a>
                    </button>
                    <p class="hint">버튼을 누르면 페이지가 전환됩니다.</p>
                </form>
            </div>
        </div>
    </main>

    <!-- 위험 영역: 전체 초기화 -->
    <div class="danger-zone">
        <div class="danger-header">
            <i class="fa-solid fa-skull-crossbones"></i>
            <h2>전체 데이터 초기화</h2>
        </div>
        <form class="ajax-form" data-action="clearAll">
            <input type="hidden" name="action" value="clearAll">
            <p class="danger-description">
                car_info, parking_times, discount_info 테이블의 모든 데이터가 삭제됩니다.<br>
                월정액 회원과 요금 정책은 삭제되지 않습니다.
            </p>
            <button type="submit" class="btn btn-danger">
                <i class="fa-solid fa-bomb"></i> 전체 초기화
            </button>
        </form>
    </div>
</div>

<!-- 로딩 오버레이 -->
<div id="loadingOverlay"
     style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:9999;justify-content:center;align-items:center;">
    <div style="background:white;padding:30px;border-radius:15px;text-align:center;">
        <div style="border:4px solid #f3f3f3;border-top:4px solid #4e73df;border-radius:50%;width:50px;height:50px;animation:spin 1s linear infinite;margin:0 auto 15px;"></div>
        <p style="margin:0;color:#333;font-weight:bold;">처리 중...</p>
    </div>
</div>

<!-- 결과 모달 -->
<div id="resultModal"
     style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:10000;justify-content:center;align-items:center;">
    <div style="background:white;padding:30px;border-radius:15px;max-width:500px;width:90%;box-shadow:0 10px 40px rgba(0,0,0,0.3);">
        <div id="modalHeader"
             style="display:flex;align-items:center;gap:10px;margin-bottom:20px;padding-bottom:15px;border-bottom:1px solid #f0f0f0;">
            <i id="modalIcon" class="fa-solid fa-circle-check" style="font-size:24px;"></i>
            <h3 id="modalTitle" style="margin:0;font-size:20px;font-weight:bold;">처리 완료</h3>
        </div>
        <div id="modalBody" style="margin-bottom:20px;line-height:1.6;color:#333;">
            <!-- 메시지 내용 -->
        </div>
        <div style="text-align:right;">
            <button onclick="closeModal()"
                    style="background:#4e73df;color:white;border:none;padding:10px 25px;border-radius:8px;cursor:pointer;font-weight:bold;">
                확인
            </button>
        </div>
    </div>
</div>

<style>
    @keyframes spin {
        0% {
            transform: rotate(0deg);
        }
        100% {
            transform: rotate(360deg);
        }
    }
</style>

<script>
    // 통계 갱신 함수
    function refreshStatistics() {
        fetch('${pageContext.request.contextPath}/api/test/statistics')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const statsContent = document.querySelector('.stats-content');
                    if (statsContent) {
                        statsContent.textContent = data.statistics;
                    }
                } else {
                    console.error('통계 조회 실패:', data.message);
                }
            })
            .catch(error => {
                console.error('통계 조회 오류:', error);
            });
    }

    // URLSearchParams 방식으로 수정 (FormData 대신)
    document.addEventListener('DOMContentLoaded', function () {
        // 페이지 로드 시 통계 즉시 호출
        refreshStatistics();

        const forms = document.querySelectorAll('.ajax-form');

        forms.forEach(form => {
            form.addEventListener('submit', function (e) {
                e.preventDefault();

                const action = this.dataset.action;

                // clearAll 액션인 경우 확인 메시지 표시
                if (action === 'clearAll') {
                    if (!confirm('정말로 모든 주차 데이터를 삭제하시겠습니까?')) {
                        return;
                    }
                }

                // FormData 대신 URLSearchParams 사용
                const formData = new FormData(this);
                const params = new URLSearchParams();

                // FormData를 URLSearchParams로 변환
                for (let pair of formData.entries()) {
                    params.append(pair[0], pair[1]);
                }

                // 로딩 표시
                showLoading();

                // AJAX 요청
                fetch('${pageContext.request.contextPath}/test', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: params.toString()
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('서버 오류 (상태 코드: ' + response.status + ')');
                        }
                        return response.text();
                    })
                    .then(data => {
                        hideLoading();

                        let message = '처리가 완료되었습니다.';
                        let isSuccess = true;

                        // HTML 파싱하여 메시지 추출
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(data, 'text/html');

                        // 성공 메시지 확인
                        const alertSuccess = doc.querySelector('.alert-success');
                        if (alertSuccess) {
                            message = alertSuccess.textContent.trim().replace(/[✓✔]/g, '').trim();
                        }

                        // 에러 메시지 확인 (있다면)
                        const alertDanger = doc.querySelector('.alert-danger');
                        if (alertDanger) {
                            message = alertDanger.textContent.trim().replace(/[✗✘]/g, '').trim();
                            isSuccess = false;
                        }

                        // 통계 업데이트
                        const statsContent = doc.querySelector('.stats-content');
                        if (statsContent) {
                            const currentStatsContent = document.querySelector('.stats-content');
                            if (currentStatsContent) {
                                currentStatsContent.textContent = statsContent.textContent;
                            }
                        }

                        showModal(isSuccess ? 'success' : 'error', isSuccess ? '처리 완료' : '처리 실패', message);
                    })
                    .catch(error => {
                        hideLoading();
                        showModal('error', '오류 발생', error.message || '요청 처리 중 오류가 발생했습니다.');
                    });
            });
        });
    });

    // 로딩 표시
    function showLoading() {
        const overlay = document.getElementById('loadingOverlay');
        overlay.style.display = 'flex';
    }

    // 로딩 숨김
    function hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        overlay.style.display = 'none';
    }

    // 결과 모달 표시
    function showModal(type, title, message) {
        const modal = document.getElementById('resultModal');
        const modalIcon = document.getElementById('modalIcon');
        const modalTitle = document.getElementById('modalTitle');
        const modalBody = document.getElementById('modalBody');

        // 아이콘 및 색상 설정
        if (type === 'success') {
            modalIcon.className = 'fa-solid fa-circle-check';
            modalIcon.style.color = '#1cc88a';
        } else {
            modalIcon.className = 'fa-solid fa-circle-exclamation';
            modalIcon.style.color = '#e74a3b';
        }

        modalTitle.textContent = title;
        modalBody.textContent = message;

        // 모달 표시
        modal.style.display = 'flex';
    }

    // 모달 닫기
    function closeModal() {
        const modal = document.getElementById('resultModal');
        modal.style.display = 'none';

        // 모달 닫을 때 통계 한 번 더 갱신
        refreshStatistics();
    }

    // 모달 배경 클릭 시 닫기
    document.addEventListener('click', function (e) {
        const modal = document.getElementById('resultModal');
        if (e.target === modal) {
            closeModal();
        }
    });
</script>

</body>
</html>
