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
                fetch('${pageContext.request.contextPath}/test/data', {
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