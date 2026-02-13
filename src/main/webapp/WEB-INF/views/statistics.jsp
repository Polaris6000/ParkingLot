<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.parkinglot.vo.StatisticsVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%
    // 숫자 포맷
    NumberFormat nf = NumberFormat.getInstance();

    // 오늘 날짜
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String today = sdf.format(new Date());

    SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
    String thisMonth = sdfMonth.format(new Date());
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>관리자 통계 대시보드 - 스마트주차 반월당점</title>

    <link rel="stylesheet" href="./static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>

<body>
<div class="container">
    <header class="dashboard-header">
        <%@include file="../web/common/header.jsp" %>
    </header>

    <div class="cards-container">
        <%
            StatisticsVO todaySummary = (StatisticsVO) request.getAttribute("todaySummary");
            StatisticsVO monthSummary = (StatisticsVO) request.getAttribute("monthSummary");
        %>
        <%-- css 변수를 활용한 card 및 occupancy 클래스 적용 --%>
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-coins"></i>
                <h2>오늘 매출</h2>
            </div>
            <div class="value"><%= todaySummary != null ? nf.format(todaySummary.getTotalAmount()) : "0" %>원</div>
            <div class="label">입차 대수: <%= todaySummary != null ? todaySummary.getTotalCount() : 0 %>대</div>
        </div>
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-calendar-check"></i>
                <h2>이번 달 매출</h2>
            </div>
            <div class="value"><%= monthSummary != null ? nf.format(monthSummary.getTotalAmount()) : "0" %>원</div>
            <div class="label">입차 대수: <%= monthSummary != null ? monthSummary.getTotalCount() : 0 %>대</div>
        </div>

        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-car-side"></i>
                <h2>오늘 이용차량 대수</h2>
            </div>
            <div class="value"><%= todaySummary != null ? todaySummary.getTotalCount() : 0 %>대</div>
            <div class="label">실시간 이용 현황</div>
        </div>
    </div>

    <div class="occupancy-section" style="margin-top: 30px;">
        <div class="tab-buttons">
            <%-- 기존 btn-success 대신 탭 전용 클래스 tab-btn 적용 --%>
            <button class="tab-btn active" onclick="showTab(event, 'daily')">일별 매출</button>
            <button class="tab-btn" onclick="showTab(event, 'monthly')">월별 매출</button>
            <button class="tab-btn" onclick="showTab(event, 'type')">차종별 통계</button>
        </div>

        <div id="daily-tab" class="tab-content active">
            <h2 class="section-title"><i class="fas fa-calendar-day" style="color: var(--primary-color)"></i> 일별 매출 통계
            </h2>

            <%-- 검색 폼 스타일을 input-group으로 정렬 --%>
            <form action="statistics" method="get" class="search-form"
                  style="display: flex; gap: 10px; align-items: center; margin-bottom: 20px;">
                <input type="hidden" name="searchType" value="daily">

                <div class="input-group" style="margin-bottom: 0;">
                    <span>시작 날짜</span>
                    <input type="date" name="startDate"
                           value="<%= request.getAttribute("startDate") != null ? request.getAttribute("startDate") : today %>"
                           required>
                </div>

                <div class="input-group" style="margin-bottom: 0;">
                    <span>종료 날짜</span>
                    <input type="date" name="endDate"
                           value="<%= request.getAttribute("endDate") != null ? request.getAttribute("endDate") : today %>"
                           required>
                </div>

                <button type="submit" class="btn-primary" style="width: auto; padding: 10px 25px;">조회</button>
            </form>

            <%
                List<StatisticsVO> dailyStats = (List<StatisticsVO>) request.getAttribute("dailyStats");
                if (dailyStats != null && !dailyStats.isEmpty()) {
            %>
            <table class="stats-table">
                <thead>
                <tr>
                    <th>날짜</th>
                    <th>총 매출</th>
                    <th>입차 대수</th>
                    <th>평균 금액</th>
                </tr>
                </thead>
                <tbody>
                <%
                    int totalDailyAmount = 0;
                    int totalDailyCount = 0;
                    for (StatisticsVO stat : dailyStats) {
                        totalDailyAmount += stat.getTotalAmount();
                        totalDailyCount += stat.getTotalCount();
                        int avgAmount = stat.getTotalCount() > 0 ? stat.getTotalAmount() / stat.getTotalCount() : 0;
                %>
                <tr>
                    <td><%= stat.getDate() %>
                    </td>
                    <td class="amount-cell"><%= nf.format(stat.getTotalAmount()) %>원</td>
                    <td><%= stat.getTotalCount() %>대</td>
                    <td><%= nf.format(avgAmount) %>원</td>
                </tr>
                <% } %>
                <tr class="table-summary" style="background: #f8f9fc; font-weight: bold;">
                    <td>합계</td>
                    <td class="amount-cell"><%= nf.format(totalDailyAmount) %>원</td>
                    <td><%= totalDailyCount %>대</td>
                    <td><%= totalDailyCount > 0 ? nf.format(totalDailyAmount / totalDailyCount) : "0" %>원</td>
                </tr>
                </tbody>
            </table>
            <% } else if ("daily".equals(request.getAttribute("searchType"))) { %>
            <div class="detail-placeholder" style="padding: 50px;">
                <i class="fas fa-exclamation-circle"></i>
                <p>해당 기간의 데이터가 없습니다.</p>
            </div>
            <% } %>
        </div>

        <div id="monthly-tab" class="tab-content">
            <h2 class="section-title"><i class="fas fa-calendar-days" style="color: var(--primary-color)"></i> 월별 매출 통계
            </h2>

            <form action="statistics" method="get" class="search-form"
                  style="display: flex; gap: 10px; align-items: center; margin-bottom: 20px;">
                <input type="hidden" name="searchType" value="monthly">

                <div class="input-group" style="margin-bottom: 0;">
                    <span>조회 월</span>
                    <input type="month" name="yearMonth"
                           value="<%= request.getAttribute("yearMonth") != null ? request.getAttribute("yearMonth") : thisMonth %>">
                </div>

                <button type="submit" class="btn-primary" style="width: auto; padding: 10px 25px;">조회</button>
                <button type="button" class="btn-info" style="width: auto; padding: 10px 25px;"
                        onclick="location.href='statistics?searchType=monthly'">전체 조회
                </button>
            </form>

            <%
                List<StatisticsVO> monthlyStats = (List<StatisticsVO>) request.getAttribute("monthlyStats");
                if (monthlyStats != null && !monthlyStats.isEmpty()) {
            %>
            <table class="stats-table">
                <thead>
                <tr>
                    <th>월</th>
                    <th>총 매출</th>
                    <th>입차 대수</th>
                    <th>평균 금액</th>
                </tr>
                </thead>
                <tbody>
                <%
                    int totalMonthlyAmount = 0;
                    int totalMonthlyCount = 0;
                    for (StatisticsVO stat : monthlyStats) {
                        totalMonthlyAmount += stat.getTotalAmount();
                        totalMonthlyCount += stat.getTotalCount();
                        int avgAmount = stat.getTotalCount() > 0 ? stat.getTotalAmount() / stat.getTotalCount() : 0;
                %>
                <tr>
                    <td><%= stat.getDate() %>
                    </td>
                    <td class="amount-cell"><%= nf.format(stat.getTotalAmount()) %>원</td>
                    <td><%= stat.getTotalCount() %>대</td>
                    <td><%= nf.format(avgAmount) %>원</td>
                </tr>
                <% } %>
                <tr class="table-summary" style="background: #f8f9fc; font-weight: bold;">
                    <td>합계</td>
                    <td class="amount-cell"><%= nf.format(totalMonthlyAmount) %>원</td>
                    <td><%= totalMonthlyCount %>대</td>
                    <td><%= totalMonthlyCount > 0 ? nf.format(totalMonthlyAmount / totalMonthlyCount) : "0" %>원</td>
                </tr>
                </tbody>
            </table>
            <% } else if ("monthly".equals(request.getAttribute("searchType"))) { %>
            <div class="detail-placeholder" style="padding: 50px;">
                <i class="fas fa-exclamation-circle"></i>
                <p>해당 월의 데이터가 없습니다.</p>
            </div>
            <% } %>
        </div>

        <div id="type-tab" class="tab-content">
            <h2 class="section-title"><i class="fas fa-car" style="color: var(--primary-color)"></i> 차종별 이용 통계</h2>

            <%
                List<StatisticsVO> typeStats = (List<StatisticsVO>) request.getAttribute("typeStats");
                if (typeStats != null && !typeStats.isEmpty()) {
                    int totalTypeCount = 0;
                    for (StatisticsVO stat : typeStats) {
                        totalTypeCount += stat.getTypeCount();
                    }
            %>
            <table class="stats-table">
                <thead>
                <tr>
                    <th>차종</th>
                    <th>이용 건수</th>
                    <th>비율</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (StatisticsVO stat : typeStats) {
                        String kindName = "미분류";
                        if (stat.getKindOfDiscount() != null) {
                            switch (stat.getKindOfDiscount()) {
                                case "normal":
                                    kindName = "일반";
                                    break;
                                case "light":
                                    kindName = "경차";
                                    break;
                                case "disabled":
                                    kindName = "장애인";
                                    break;
                                case "monthly":
                                    kindName = "월정액";
                                    break;
                                default:
                                    kindName = stat.getKindOfDiscount();
                            }
                        }
                %>
                <tr>
                    <td><strong><%= kindName %>
                    </strong></td>
                    <td><%= stat.getTypeCount() %>건</td>
                    <td><%= String.format("%.2f", stat.getTypePercentage()) %>%</td>
                </tr>
                <% } %>
                <tr class="table-summary" style="background: #f8f9fc; font-weight: bold;">
                    <td>합계</td>
                    <td><%= totalTypeCount %>건</td>
                    <td>100%</td>
                </tr>
                </tbody>
            </table>

            <div class="chart-container"
                 style="margin-top: 30px; background: #fff; padding: 20px; border-radius: 12px;">
                <h3 style="margin-bottom: 20px; color: #333;">차종별 이용 비율 차트</h3>
                <%
                    for (StatisticsVO stat : typeStats) {
                        String barClass = "bar-normal";
                        String kindName = "미분류";
                        if (stat.getKindOfDiscount() != null) {
                            switch (stat.getKindOfDiscount()) {
                                case "normal":
                                    kindName = "일반";
                                    barClass = "bar-normal";
                                    break;
                                case "light":
                                    kindName = "경차";
                                    barClass = "bar-light";
                                    break;
                                case "disabled":
                                    kindName = "장애인";
                                    barClass = "bar-disabled";
                                    break;
                                case "monthly":
                                    kindName = "월정액";
                                    barClass = "bar-monthly";
                                    break;
                                default:
                                    kindName = stat.getKindOfDiscount();
                            }
                        }
                %>
                <div class="type-bar-row" style="display: flex; align-items: center; gap: 15px; margin-bottom: 15px;">
                    <div class="type-label" style="width: 80px; font-weight: bold;"><%= kindName %>
                    </div>
                    <div class="bar-bg"
                         style="flex: 1; background: #eee; height: 25px; border-radius: 12px; overflow: hidden;">
                        <div class="bar-fill <%= barClass %>"
                             style="width: <%= stat.getTypePercentage() %>%; height: 100%; display: flex; align-items: center; justify-content: flex-end; padding-right: 10px; color: white; font-size: 12px; font-weight: bold;">
                            <%= String.format("%.1f", stat.getTypePercentage()) %>%
                        </div>
                    </div>
                    <div class="type-count" style="width: 60px; text-align: right;"><%= stat.getTypeCount() %>건</div>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="detail-placeholder" style="padding: 50px;">
                <i class="fas fa-info-circle"></i>
                <p>차종별 통계 데이터가 없습니다.</p>
            </div>
            <% } %>
        </div>
    </div>
</div>

<script>
    // 탭 전환 함수
    function showTab(event, tabName) {
        // 모든 탭 버튼 비활성화
        const tabButtons = document.querySelectorAll('.tab-btn');
        tabButtons.forEach(btn => btn.classList.remove('active'));

        // 모든 탭 컨텐츠 숨김
        const tabContents = document.querySelectorAll('.tab-content');
        tabContents.forEach(content => content.classList.remove('active'));

        // 선택된 탭 활성화
        event.currentTarget.classList.add('active');
        document.getElementById(tabName + '-tab').classList.add('active');
    }

    // 페이지 로드 시 검색 타입에 따라 탭 활성화
    window.onload = function () {
        const searchType = '<%= request.getAttribute("searchType") %>';
        if (searchType === 'monthly') {
            showTabByName('monthly');
        } else if (searchType === 'daily') {
            showTabByName('daily');
        } else {
            // 기본값은 일별 탭
            showTabByName('daily');
        }
    };

    function showTabByName(tabName) {
        const tabButtons = document.querySelectorAll('.tab-btn');
        const tabContents = document.querySelectorAll('.tab-content');

        tabButtons.forEach(btn => btn.classList.remove('active'));
        tabContents.forEach(content => content.classList.remove('active'));

        if (tabName === 'daily') {
            tabButtons[0].classList.add('active');
            document.getElementById('daily-tab').classList.add('active');
        } else if (tabName === 'monthly') {
            tabButtons[1].classList.add('active');
            document.getElementById('monthly-tab').classList.add('active');
        } else if (tabName === 'type') {
            tabButtons[2].classList.add('active');
            document.getElementById('type-tab').classList.add('active');
        }
    }
</script>
</body>
</html>