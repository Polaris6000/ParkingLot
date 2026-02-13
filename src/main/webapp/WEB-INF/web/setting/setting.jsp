<%@ page contentType="text/html; charset=UTF-8" language="java" %>
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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Noto Sans KR', 'Malgun Gothic', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #333;
        }

        /* 헤더 스타일 */
        header {
            background-color: #2c3e50;
            color: white;
            padding: 20px 0;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        header h1 {
            text-align: center;
            font-size: 28px;
            margin-bottom: 15px;
            font-weight: 700;
        }

        /* 네비게이션 스타일 */
        nav {
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
            padding: 0 20px;
        }

        nav a {
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            background-color: #34495e;
            border-radius: 8px;
            transition: all 0.3s ease;
            font-weight: 500;
            font-size: 14px;
        }

        nav a:hover {
            background-color: #3498db;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }

        nav a.active {
            background-color: #e74c3c;
            font-weight: 700;
        }

        /* 컨테이너 */
        .container {
            max-width: 1400px;
            margin: 30px auto;
            padding: 0 20px;
        }

        /* 페이지 타이틀 */
        .page-title {
            font-size: 32px;
            margin-bottom: 30px;
            color: white;
            text-align: center;
            font-weight: 700;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        /* 알림 메시지 */
        .alert {
            padding: 15px 20px;
            margin-bottom: 25px;
            border-radius: 8px;
            font-size: 15px;
            animation: slideDown 0.4s ease;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border-left: 5px solid #28a745;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border-left: 5px solid #dc3545;
        }

        /* 카드 스타일 */
        .card {
            background-color: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            margin-bottom: 30px;
        }

        .card h2 {
            font-size: 22px;
            margin-bottom: 25px;
            color: #2c3e50;
            border-left: 5px solid #3498db;
            padding-left: 15px;
            font-weight: 700;
        }

        /* 폼 스타일 */
        .form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #555;
            font-size: 14px;
        }

        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 15px;
            transition: all 0.3s ease;
            background-color: #f8f9fa;
        }

        .form-group input:focus {
            outline: none;
            border-color: #3498db;
            background-color: white;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }

        .form-group .help-text {
            font-size: 12px;
            color: #6c757d;
            margin-top: 5px;
            font-style: italic;
        }

        /* 버튼 스타일 */
        .btn {
            padding: 13px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
            transform: translateY(-2px);
        }

        .btn-group {
            display: flex;
            gap: 15px;
            margin-top: 25px;
        }

        /* 테이블 스타일 */
        .table-container {
            overflow-x: auto;
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
        }

        table thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        table th {
            padding: 15px 10px;
            text-align: center;
            font-weight: 600;
            font-size: 14px;
            white-space: nowrap;
        }

        table td {
            padding: 12px 10px;
            text-align: center;
            border-bottom: 1px solid #e9ecef;
            color: #495057;
            font-size: 13px;
        }

        table tbody tr {
            transition: background-color 0.2s ease;
        }

        table tbody tr:hover {
            background-color: #f8f9fa;
        }

        .current-badge {
            display: inline-block;
            padding: 4px 12px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            margin-left: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        .no-data {
            text-align: center;
            padding: 50px;
            color: #6c757d;
            font-size: 16px;
        }

        /* 통계 정보 */
        .stats-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border-radius: 8px;
            color: white;
            margin-bottom: 20px;
        }

        .stats-info span {
            font-size: 14px;
            font-weight: 600;
        }

        /* 반응형 디자인 */
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }

            nav {
                flex-direction: column;
                align-items: center;
            }

            .btn-group {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }

            table {
                font-size: 12px;
            }

            table th, table td {
                padding: 8px 5px;
            }

            .page-title {
                font-size: 24px;
            }

            .stats-info {
                flex-direction: column;
                gap: 10px;
            }
        }

        /* 스크롤바 스타일 */
        ::-webkit-scrollbar {
            width: 10px;
        }

        ::-webkit-scrollbar-track {
            background: #f1f1f1;
        }

        ::-webkit-scrollbar-thumb {
            background: #888;
            border-radius: 5px;
        }

        ::-webkit-scrollbar-thumb:hover {
            background: #555;
        }
    </style>
</head>
<body>
<!-- 헤더 영역 -->
<header>
    <h1>🅿️ 스마트주차 반월당점 관리 시스템</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/dashboard">🏠 주차 현황</a>
        <a href="${pageContext.request.contextPath}/entry">🚗 입차 관리</a>
        <a href="${pageContext.request.contextPath}/exit">🚙 출차 관리</a>
        <a href="${pageContext.request.contextPath}/member">👥 회원 관리</a>
        <a href="${pageContext.request.contextPath}/stats">📊 통계</a>
        <a href="${pageContext.request.contextPath}/setting" class="active">⚙️ 요금 설정</a>
    </nav>
</header>

<!-- 메인 컨테이너 -->
<div class="container">
    <h1 class="page-title">⚙️ 요금 정책 설정 관리</h1>

    <!-- 알림 메시지 -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
            ✔ ${successMessage}
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            ✗ ${errorMessage}
        </div>
    </c:if>

    <!-- 요금 정책 등록 폼 -->
    <div class="card">
        <h2>📝 새로운 요금 정책 등록</h2>
        <form action="${pageContext.request.contextPath}/setting" method="post" id="settingForm">
            <input type="hidden" name="action" value="insert">

            <!-- 기본 요금 정보 -->
            <div class="form-row">
                <div class="form-group">
                    <label for="baseFee">💰 기본 요금 (원)</label>
                    <input type="number" id="baseFee" name="baseFee" value="2000" min="0" step="100" required>
                    <div class="help-text">최초 1시간 요금 (10분 초과 ~ 1시간 이내)</div>
                </div>

                <div class="form-group">
                    <label for="basicUnitMinute">⏱️ 기본 시간 (분)</label>
                    <input type="number" id="basicUnitMinute" name="basicUnitMinute" value="60" min="1" required>
                    <div class="help-text">기본 요금 적용 시간</div>
                </div>
            </div>

            <!-- 추가 요금 정보 -->
            <div class="form-row">
                <div class="form-group">
                    <label for="unitFee">💵 추가 요금 (원)</label>
                    <input type="number" id="unitFee" name="unitFee" value="1000" min="0" step="100" required>
                    <div class="help-text">기본 시간 이후 단위 시간당 추가 요금</div>
                </div>

                <div class="form-group">
                    <label for="billingUnitMinutes">⏲️ 추가 과금 단위 (분)</label>
                    <input type="number" id="billingUnitMinutes" name="billingUnitMinutes" value="30" min="1" required>
                    <div class="help-text">추가 요금 부과 시간 단위 (30분당)</div>
                </div>
            </div>

            <!-- 할인율 정보 -->
            <div class="form-row">
                <div class="form-group">
                    <label for="helpDiscountRate">♿ 장애인 할인율 (%)</label>
                    <input type="number" id="helpDiscountRate" name="helpDiscountRate" value="50" min="0" max="100"
                           required>
                    <div class="help-text">장애인 차량 할인율 (0~100%)</div>
                </div>

                <div class="form-group">
                    <label for="compactDiscountRate">🚗 경차 할인율 (%)</label>
                    <input type="number" id="compactDiscountRate" name="compactDiscountRate" value="30" min="0"
                           max="100" required>
                    <div class="help-text">경차 할인율 (0~100%)</div>
                </div>
            </div>

            <!-- 기타 정책 -->
            <div class="form-row">
                <div class="form-group">
                    <label for="gracePeriodMinutes">🔄 회차 인정 시간 (분)</label>
                    <input type="number" id="gracePeriodMinutes" name="gracePeriodMinutes" value="10" min="0" required>
                    <div class="help-text">입차 후 이 시간 이내 출차 시 요금 0원</div>
                </div>

                <div class="form-group">
                    <label for="maxCapAmount">📌 일일 최대 요금 (원)</label>
                    <input type="number" id="maxCapAmount" name="maxCapAmount" value="15000" min="0" step="1000"
                           required>
                    <div class="help-text">24시간 기준 최대 요금 (cap)</div>
                </div>
            </div>

            <!-- 버튼 그룹 -->
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">💾 정책 등록</button>
                <button type="reset" class="btn btn-secondary">🔄 초기화</button>
            </div>
        </form>
    </div>

    <!-- 요금 정책 목록 -->
    <div class="card">
        <h2>📋 현재 적용 중인 요금 정책</h2>

        <!-- 통계 정보 -->
        <c:if test="${not empty currentPolicy}">
            <div class="stats-info">
                <span>📌 현재 적용 중인 요금 정책입니다.</span>
                <span>🕐 최근 변경: ${currentPolicy.updateDate}</span>
            </div>
        </c:if>

        <div class="table-container">
            <c:choose>
                <c:when test="${not empty currentPolicy}">
                    <table>
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
                                <span class="current-badge">현재</span>
                            </td>
                            <td><strong>${currentPolicy.baseFee}</strong>원</td>
                            <td>${currentPolicy.basicUnitMinute}분</td>
                            <td><strong>${currentPolicy.unitFee}</strong>원</td>
                            <td>${currentPolicy.billingUnitMinutes}분</td>
                            <td><span
                                    style="color: #e74c3c; font-weight: bold;">${currentPolicy.helpDiscountRate}%</span>
                            </td>
                            <td><span
                                    style="color: #3498db; font-weight: bold;">${currentPolicy.compactDiscountRate}%</span>
                            </td>
                            <td>${currentPolicy.gracePeriodMinutes}분</td>
                            <td><strong>${currentPolicy.maxCapAmount}</strong>원</td>
                            <td>${currentPolicy.updateDate}</td>
                        </tr>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                </c:otherwise>
            </c:choose>
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
            '• 기본 요금: ' + baseFee + '원\n' +
            '• 추가 요금: ' + unitFee + '원\n' +
            '• 장애인 할인: ' + helpDiscount + '%\n' +
            '• 경차 할인: ' + compactDiscount + '%\n' +
            '• 최대 요금: ' + maxCap + '원';

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
</script>
</body>
</html>
