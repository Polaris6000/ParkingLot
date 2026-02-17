<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 디버깅: 전달된 데이터 확인
    System.out.println("===== JSP 디버깅 정보 =====");
    System.out.println("policyList: " + request.getAttribute("policyList"));
    System.out.println("currentPolicy: " + request.getAttribute("currentPolicy"));
    System.out.println("policyCount: " + request.getAttribute("policyCount"));

    java.util.List policyList = (java.util.List) request.getAttribute("policyList");
    if (policyList != null) {
        System.out.println("JSP - policyList 크기: " + policyList.size());
    } else {
        System.out.println("JSP - policyList가 null입니다!");
    }
    System.out.println("==========================");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>스마트주차 반월당점 - 요금 정책 설정</title>

    <link rel="stylesheet" href="./static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- 메인 컨테이너 -->
<div class="container">

    <!-- 헤더 영역 -->
    <header class="dashboard-header">
        <%@include file="../../web/common/header.jsp" %>
    </header>

    <!-- 요금 정책 등록 폼 -->
    <div class="card policy-form-card">
        <form action="${pageContext.request.contextPath}/setting" method="post" id="settingForm">
            <input type="hidden" name="action" value="insert">

            <!-- 기본 요금 정보 -->
            <h3 class="section-title">
                <i class="fas fa-coins"></i>
                기본 요금 정보
            </h3>
            <div class="form-row">
                <div class="form-group">
                    <label for="baseFee">
                        <i class="fas fa-won-sign"></i> 기본 요금 (원)
                    </label>
                    <input type="number" id="baseFee" name="baseFee" value="2000" min="0" step="100" required>
                    <div class="help-text">최초 1시간 요금 (10분 초과 ~ 1시간 이내)</div>
                </div>

                <div class="form-group">
                    <label for="basicUnitMinute">
                        <i class="fas fa-clock"></i> 기본 시간 (분)
                    </label>
                    <input type="number" id="basicUnitMinute" name="basicUnitMinute" value="60" min="1" required>
                    <div class="help-text">기본 요금 적용 시간</div>
                </div>
            </div>

            <!-- 추가 요금 정보 -->
            <h3 class="section-title">
                <i class="fas fa-calculator"></i>
                추가 요금 정보
            </h3>
            <div class="form-row">
                <div class="form-group">
                    <label for="unitFee">
                        <i class="fas fa-money-bill-wave"></i> 추가 요금 (원)
                    </label>
                    <input type="number" id="unitFee" name="unitFee" value="1000" min="0" step="100" required>
                    <div class="help-text">기본 시간 이후 단위 시간당 추가 요금</div>
                </div>

                <div class="form-group">
                    <label for="billingUnitMinutes">
                        <i class="fas fa-hourglass-half"></i> 추가 과금 단위 (분)
                    </label>
                    <input type="number" id="billingUnitMinutes" name="billingUnitMinutes" value="30" min="1" required>
                    <div class="help-text">추가 요금 부과 시간 단위 (30분당)</div>
                </div>
            </div>

            <!-- 할인율 정보 -->
            <h3 class="section-title">
                <i class="fas fa-percent"></i>
                할인 정책
            </h3>
            <div class="form-row">
                <div class="form-group">
                    <label for="helpDiscountRate">
                        <i class="fas fa-wheelchair"></i> 장애인 할인율 (%)
                    </label>
                    <input type="number" id="helpDiscountRate" name="helpDiscountRate" value="50" min="0" max="100"
                           required>
                    <div class="help-text">장애인 차량 할인율 (0~100%)</div>
                </div>

                <div class="form-group">
                    <label for="compactDiscountRate">
                        <i class="fas fa-car-side"></i> 경차 할인율 (%)
                    </label>
                    <input type="number" id="compactDiscountRate" name="compactDiscountRate" value="30" min="0"
                           max="100" required>
                    <div class="help-text">경차 할인율 (0~100%)</div>
                </div>
            </div>

            <!-- 기타 정책 -->
            <h3 class="section-title">
                <i class="fas fa-cog"></i>
                기타 정책
            </h3>
            <div class="form-row">
                <div class="form-group">
                    <label for="gracePeriodMinutes">
                        <i class="fas fa-rotate"></i> 회차 인정 시간 (분)
                    </label>
                    <input type="number" id="gracePeriodMinutes" name="gracePeriodMinutes" value="10" min="0" required>
                    <div class="help-text">입차 후 이 시간 이내 출차 시 요금 0원</div>
                </div>

                <div class="form-group">
                    <label for="maxCapAmount">
                        <i class="fas fa-hand-holding-dollar"></i> 일일 최대 요금 (원)
                    </label>
                    <input type="number" id="maxCapAmount" name="maxCapAmount" value="15000" min="0" step="1000"
                           required>
                    <div class="help-text">24시간 기준 최대 요금 (cap)</div>
                </div>
            </div>

            <!-- 버튼 그룹 -->
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> 정책 등록
                </button>
                <button type="reset" class="btn btn-secondary">
                    <i class="fas fa-redo"></i> 초기화
                </button>
            </div>
        </form>
    </div>

    <!-- 요금 정책 목록 -->
    <div class="card policy-list-card">
        <div class="card-header">
            <i class="fas fa-list-check"></i>
            <h2>현재 적용 중인 요금 정책</h2>
        </div>

        <!-- 통계 정보 -->
        <c:if test="${not empty currentPolicy}">
            <div class="status-banner">
                <span class="status-text">
                    <i class="fas fa-check-circle"></i>
                    현재 적용 중인 요금 정책입니다.
                </span>
                <span class="update-time">
                    <i class="fas fa-clock"></i>
                    최근 변경: ${currentPolicy.updateDate}
                </span>
            </div>
        </c:if>

        <div class="table-container">
            <c:if test="${not empty currentPolicy && currentPolicy.id > 0}">
                <table class="policy-table">
                    <thead>
                    <tr>
                        <th>번호</th>
                        <th>기본요금</th>
                        <th>기본시간</th>
                        <th>추가요금</th>
                        <th>과금단위</th>
                        <th>장애인할인</th>
                        <th>경차할인</th>
                        <th>회차시간</th>
                        <th>최대요금</th>
                        <th>변경일시</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <strong>${currentPolicy.id}</strong>
                                <%--                                <span class="current-badge">현재</span>--%>
                        </td>
                        <td class="amount-cell"><strong>${currentPolicy.baseFee}</strong>원</td>
                        <td>${currentPolicy.basicUnitMinute}분</td>
                        <td class="amount-cell"><strong>${currentPolicy.unitFee}</strong>원</td>
                        <td>${currentPolicy.billingUnitMinutes}분</td>
                        <td class="discount-danger">${currentPolicy.helpDiscountRate}%</td>
                        <td class="discount-info">${currentPolicy.compactDiscountRate}%</td>
                        <td>${currentPolicy.gracePeriodMinutes}분</td>
                        <td class="amount-cell"><strong>${currentPolicy.maxCapAmount}</strong>원</td>
                        <td class="date-cell">${currentPolicy.updateDate}</td>
                    </tr>
                    </tbody>
                </table>
            </c:if>
            <%-- 기존에 <c:otherwise> 였었는데 데이터가 있어도 계속 뜨길래 제가 수정했어요. 그런데도 계속 뜨네요..?--%>
            <c:if test="${empty currentPolicy || currentPolicy.id <= 0}">
                <div class="no-data">
                    <i class="fas fa-inbox"></i>
                    <p>등록된 요금 정책이 없습니다.</p>
                </div>
            </c:if>
        </div>
    </div>

    <!-- 알림 메시지 -->
    <!-- 결과 모달 -->
    <div id="resultModal"
         style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:10000;justify-content:center;align-items:center;">
        <div style="background:white;padding:30px;border-radius:15px;max-width:500px;width:90%;box-shadow:0 10px 40px rgba(0,0,0,0.3);">
            <div id="modalHeader"
                 style="display:flex;align-items:center;gap:10px;margin-bottom:20px;padding-bottom:15px;border-bottom:1px solid #f0f0f0;">
                <i id="modalIcon" class="fa-solid fa-circle-check" style="font-size:24px;"></i>
                <h3 id="modalTitle" style="margin:0;font-size:20px;font-weight:bold;">처리 완료</h3>
            </div>
            <div id="modalBody" style="margin-bottom:20px;line-height:1.6;color:#333;"></div>
            <div style="text-align:right;">
                <button onclick="closeSettingModal()"
                        style="background:#4e73df;color:white;border:none;padding:10px 25px;border-radius:8px;cursor:pointer;font-weight:bold;width:auto;">
                    확인
                </button>
            </div>
        </div>
    </div>

</div>


<!-- JavaScript -->
<script>
    // 폼 제출 전 유효성 검사
    document.getElementById('settingForm').addEventListener('submit', function (e) {
        const baseFee = parseInt(document.getElementById('baseFee').value);
        const unitFee = parseInt(document.getElementById('unitFee').value);
        const helpDiscount = parseInt(document.getElementById('helpDiscountRate').value);
        const compactDiscount = parseInt(document.getElementById('compactDiscountRate').value);
        const maxCap = parseInt(document.getElementById('maxCapAmount').value);

        // 할인율 범위 체크
        if (helpDiscount < 0 || helpDiscount > 100) {
            alert('장애인 할인율은 0~100% 범위로 입력해주세요.');
            e.preventDefault();
            return false;
        }

        if (compactDiscount < 0 || compactDiscount > 100) {
            alert('경차 할인율은 0~100% 범위로 입력해주세요.');
            e.preventDefault();
            return false;
        }

        // 최대 요금이 기본 요금보다 낮은지 체크
        if (maxCap > 0 && maxCap < baseFee) {
            alert('최대 요금은 기본 요금보다 높아야 합니다.');
            e.preventDefault();
            return false;
        }

        // 확인 메시지
        const confirmMsg = '다음 내용으로 요금 정책을 등록하시겠습니까?\n\n' +
            '• 기본 요금: ' + baseFee.toLocaleString() + '원\n' +
            '• 추가 요금: ' + unitFee.toLocaleString() + '원\n' +
            '• 장애인 할인: ' + helpDiscount + '%\n' +
            '• 경차 할인: ' + compactDiscount + '%\n' +
            '• 최대 요금: ' + maxCap.toLocaleString() + '원';

        if (!confirm(confirmMsg)) {
            e.preventDefault();
            return false;
        }
    });

    // 알림 메시지 자동 숨김
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function (alert) {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.style.display = 'none';
            }, 500);
        });
    }, 5000);

    // 숫자 입력 필드에 천 단위 구분 쉼표 추가 (선택사항)
    document.querySelectorAll('input[type="number"]').forEach(input => {
        input.addEventListener('blur', function () {
            if (this.value && (this.id === 'baseFee' || this.id === 'unitFee' || this.id === 'maxCapAmount')) {
                // 입력값을 유지하면서 시각적 피드백만 제공
                console.log('입력된 금액:', parseInt(this.value).toLocaleString() + '원');
            }
        });
    });

    // 페이지 로드 시 메시지 있으면 모달 표시
    window.addEventListener('DOMContentLoaded', function () {
        <c:if test="${not empty successMessage}">
        showSettingModal('success', '등록 완료', '${successMessage}');
        </c:if>
        <c:if test="${not empty errorMessage}">
        showSettingModal('error', '오류 발생', '${errorMessage}');
        </c:if>
    });

    function showSettingModal(type, title, message) {
        const modal = document.getElementById('resultModal');
        const icon = document.getElementById('modalIcon');
        document.getElementById('modalTitle').textContent = title;
        document.getElementById('modalBody').textContent = message;

        if (type === 'success') {
            icon.className = 'fa-solid fa-circle-check';
            icon.style.color = '#1cc88a';
        } else {
            icon.className = 'fa-solid fa-circle-exclamation';
            icon.style.color = '#e74a3b';
        }

        modal.style.display = 'flex';
    }

    function closeSettingModal() {
        document.getElementById('resultModal').style.display = 'none';
    }

    // 모달 배경 클릭 시 닫기
    document.addEventListener('click', function (e) {
        const modal = document.getElementById('resultModal');
        if (e.target === modal) closeSettingModal();
    });

</script>
</body>
</html>
