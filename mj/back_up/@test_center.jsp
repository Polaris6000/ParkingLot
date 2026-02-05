<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.exmple.parkinglot.service.TestDataService" %>
<%@ page import="java.util.Optional" %>
<%
    // 버튼 클릭 시 로직 처리
    String action = request.getParameter("action");
    String message = "";
    String alertClass = "info";

    if (action != null) {
        TestDataService service = new TestDataService();
        try {
            switch (action) {
                case "clear":
                    service.clearAllData();
                    message = "DB 초기화 완료! 모든 데이터가 삭제되었습니다.";
                    alertClass = "danger";
                    break;
                case "policy":
                    service.insertFeePolicy(2000, 1000);
                    message = "기본 요금 정책(2,000원/1,000원)이 등록되었습니다.";
                    break;
                case "members":
                    service.registerTestMembers(50);
                    message = "월정액 회원 50명 대량 등록 완료!";
                    break;
                case "fill":
                    service.fillParkingSpots();
                    message = "주차장 20개 구역 만차 시뮬레이션 완료!";
                    alertClass = "success";
                    break;
                case "full_scenario":
                    service.generateFullTestScenario();
                    message = "종합 테스트 시나리오(정책+회원+과거기록+만차) 실행 완료!";
                    alertClass = "success";
                    break;
            }
        } catch (Exception e) {
            message = "오류 발생: " + e.getMessage();
            alertClass = "danger";
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>테스트 데이터 센터</title>
    <link rel="stylesheet" href="../static/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* 대시보드 CSS 외 추가 스타일 */
        .alert-box {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: bold;
            text-align: center;
        }
        .info { background: #e7f0ff; color: #3b5bdb; }
        .success { background: #f6ffed; color: #52c41a; border: 1px solid #b7eb8f; }
        .danger { background: #fff1f0; color: #cf1322; border: 1px solid #ffa39e; }

        .btn-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; width: 100%; }
        .action-btn {
            padding: 20px; border: none; border-radius: 12px; font-size: 16px;
            font-weight: bold; cursor: pointer; transition: 0.3s;
            display: flex; flex-direction: column; align-items: center; gap: 10px;
        }
        .action-btn i { font-size: 24px; }
        .btn-blue { background: var(--accent-blue); color: white; }
        .btn-gray { background: #f1f3f5; color: #495057; }
        .btn-red { background: #ff4d4f; color: white; }
        .action-btn:hover { opacity: 0.8; transform: translateY(-3px); }
    </style>
</head>
<body>
    <div class="container">
        <header class="dashboard-header">
            <div class="header-left">
                <h1>테스트 데이터 관리 센터</h1>
                <p class="label">프로젝트 개발 및 시뮬레이션 전용 도구</p>
            </div>
            <div class="header-buttons">
                <button class="exit-btn" onclick="location.href='dash_board.jsp'">대시보드로 돌아가기</button>
            </div>
        </header>

        <div class="main-content">
            <div class="left-panel">
                <% if (!message.isEmpty()) { %>
                    <div class="alert-box <%=alertClass%>">
                        <%= message %>
                    </div>
                <% } %>

                <div class="card full">
                    <div class="section-header">
                        <h3>데이터 생성 옵션</h3>
                    </div>
                    <form method="post" class="btn-grid">
                        <button type="submit" name="action" value="policy" class="action-btn btn-gray">
                            <i class="fa-solid fa-file-invoice-dollar"></i>
                            요금 정책 등록
                        </button>
                        <button type="submit" name="action" value="members" class="action-btn btn-gray">
                            <i class="fa-solid fa-users"></i>
                            회원 대량 등록
                        </button>
                        <button type="submit" name="action" value="fill" class="action-btn btn-blue">
                            <i class="fa-solid fa-car"></i>
                            20구역 만차 생성
                        </button>
                        <button type="submit" name="action" value="full_scenario" class="action-btn btn-blue">
                            <i class="fa-solid fa-database"></i>
                            종합 시나리오 실행
                        </button>
                    </form>
                </div>

                <div class="card full" style="margin-top: 20px;">
                    <div class="section-header">
                        <h3 style="color: #cf1322;"><i class="fa-solid fa-triangle-exclamation"></i> 위험 구역</h3>
                    </div>
                    <p class="label">초기화 시 모든 테이블의 데이터가 삭제되며 되돌릴 수 없습니다.</p>
                    <form method="post" onsubmit="return confirm('정말로 모든 데이터를 지우겠습니까?');">
                        <button type="submit" name="action" value="clear" class="action-btn btn-red" style="width: 100%;">
                            <i class="fa-solid fa-trash-can"></i>
                            데이터베이스 전체 초기화
                        </button>
                    </form>
                </div>
            </div>

            <div class="right-panel">
                <div class="detail-placeholder">
                    <i class="fa-solid fa-circle-info"></i>
                    <h3>도움말</h3>
                    <p>1. 먼저 <b>전체 초기화</b>를 실행하세요.</p>
                    <p>2. <b>종합 시나리오</b>를 누르면 모든 데이터가 세팅됩니다.</p>
                    <p>3. 상단 버튼을 통해 메인 대시보드로 이동하여<br>시각화된 주차 현황을 확인하세요.</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>