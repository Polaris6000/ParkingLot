<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
        }
        
        .header {
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-bottom: 30px;
            text-align: center;
        }
        
        .header h1 {
            color: #333;
            font-size: 32px;
            margin-bottom: 10px;
        }
        
        .header p {
            color: #666;
            font-size: 16px;
        }
        
        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .summary-card {
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        
        .summary-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        
        .summary-card h3 {
            color: #666;
            font-size: 14px;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        
        .summary-card .amount {
            color: #667eea;
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .summary-card .count {
            color: #999;
            font-size: 14px;
        }
        
        .content-section {
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .section-title {
            color: #333;
            font-size: 24px;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 3px solid #667eea;
        }
        
        .search-form {
            display: flex;
            gap: 15px;
            margin-bottom: 25px;
            flex-wrap: wrap;
            align-items: flex-end;
        }
        
        .form-group {
            flex: 1;
            min-width: 200px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #666;
            font-size: 14px;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .btn {
            padding: 10px 25px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: bold;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        .stats-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        .stats-table th,
        .stats-table td {
            padding: 15px;
            text-align: center;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .stats-table th {
            background: #f8f9fa;
            color: #333;
            font-weight: bold;
            text-transform: uppercase;
            font-size: 13px;
        }
        
        .stats-table tr:hover {
            background: #f8f9fa;
        }
        
        .stats-table td {
            color: #666;
        }
        
        .amount-cell {
            color: #667eea;
            font-weight: bold;
            font-size: 16px;
        }
        
        .chart-container {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
        }
        
        .type-bar {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .type-label {
            width: 80px;
            font-weight: bold;
            color: #333;
        }
        
        .bar-container {
            flex: 1;
            background: #e0e0e0;
            height: 30px;
            border-radius: 15px;
            overflow: hidden;
            position: relative;
        }
        
        .bar-fill {
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            padding-right: 10px;
            color: white;
            font-weight: bold;
            font-size: 12px;
            transition: width 1s ease;
        }
        
        .bar-normal { background: #667eea; }
        .bar-light { background: #48bb78; }
        .bar-disabled { background: #ed8936; }
        .bar-monthly { background: #9f7aea; }
        
        .type-count {
            width: 100px;
            text-align: right;
            color: #666;
            font-size: 14px;
        }
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
            font-size: 16px;
        }
        
        .back-btn {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 30px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            transition: all 0.3s;
        }
        
        .back-btn:hover {
            background: #5a6268;
            transform: translateY(-2px);
        }
        
        .tab-buttons {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .tab-btn {
            flex: 1;
            padding: 12px;
            border: 2px solid #667eea;
            background: white;
            color: #667eea;
            cursor: pointer;
            border-radius: 8px;
            font-weight: bold;
            transition: all 0.3s;
        }
        
        .tab-btn.active {
            background: #667eea;
            color: white;
        }
        
        .tab-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
        }
        
        .tab-content {
            display: none;
        }
        
        .tab-content.active {
            display: block;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 헤더 -->
        <div class="header">
            <h1>📊 관리자 통계 대시보드</h1>
            <p>스마트주차 반월당점 - 실시간 매출 및 운영 현황</p>
        </div>
        
        <!-- 요약 카드 -->
        <div class="summary-cards">
            <%
                StatisticsVO todaySummary = (StatisticsVO) request.getAttribute("todaySummary");
                StatisticsVO monthSummary = (StatisticsVO) request.getAttribute("monthSummary");
            %>
            <div class="summary-card">
                <h3>오늘 매출</h3>
                <div class="amount"><%= todaySummary != null ? nf.format(todaySummary.getTotalAmount()) : "0" %>원</div>
                <div class="count">입차 대수: <%= todaySummary != null ? todaySummary.getTotalCount() : 0 %>대</div>
            </div>
            
            <div class="summary-card">
                <h3>이번 달 매출</h3>
                <div class="amount"><%= monthSummary != null ? nf.format(monthSummary.getTotalAmount()) : "0" %>원</div>
                <div class="count">입차 대수: <%= monthSummary != null ? monthSummary.getTotalCount() : 0 %>대</div>
            </div>
            
            <div class="summary-card">
                <h3>총 주차면수</h3>
                <div class="amount">20면</div>
                <div class="count">구역: A-1 ~ A-20</div>
            </div>
        </div>
        
        <!-- 탭 버튼 -->
        <div class="content-section">
            <div class="tab-buttons">
                <button class="tab-btn active" onclick="showTab('daily')">일별 매출</button>
                <button class="tab-btn" onclick="showTab('monthly')">월별 매출</button>
                <button class="tab-btn" onclick="showTab('type')">차종별 통계</button>
            </div>
            
            <!-- 일별 매출 탭 -->
            <div id="daily-tab" class="tab-content active">
                <h2 class="section-title">📅 일별 매출 통계</h2>
                
                <form action="statistics" method="get" class="search-form">
                    <input type="hidden" name="searchType" value="daily">
                    
                    <div class="form-group">
                        <label>시작 날짜</label>
                        <input type="date" name="startDate" value="<%= request.getAttribute("startDate") != null ? request.getAttribute("startDate") : today %>" required>
                    </div>
                    
                    <div class="form-group">
                        <label>종료 날짜</label>
                        <input type="date" name="endDate" value="<%= request.getAttribute("endDate") != null ? request.getAttribute("endDate") : today %>" required>
                    </div>
                    
                    <button type="submit" class="btn btn-primary">조회</button>
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
                                <td><%= stat.getDate() %></td>
                                <td class="amount-cell"><%= nf.format(stat.getTotalAmount()) %>원</td>
                                <td><%= stat.getTotalCount() %>대</td>
                                <td><%= nf.format(avgAmount) %>원</td>
                            </tr>
                            <% } %>
                            <tr style="background: #f0f0f0; font-weight: bold;">
                                <td>합계</td>
                                <td class="amount-cell"><%= nf.format(totalDailyAmount) %>원</td>
                                <td><%= totalDailyCount %>대</td>
                                <td><%= totalDailyCount > 0 ? nf.format(totalDailyAmount / totalDailyCount) : "0" %>원</td>
                            </tr>
                        </tbody>
                    </table>
                <% } else if ("daily".equals(request.getAttribute("searchType"))) { %>
                    <div class="no-data">해당 기간의 데이터가 없습니다.</div>
                <% } %>
            </div>
            
            <!-- 월별 매출 탭 -->
            <div id="monthly-tab" class="tab-content">
                <h2 class="section-title">📆 월별 매출 통계</h2>
                
                <form action="statistics" method="get" class="search-form">
                    <input type="hidden" name="searchType" value="monthly">
                    
                    <div class="form-group">
                        <label>조회 월 (선택하지 않으면 전체 조회)</label>
                        <input type="month" name="yearMonth" value="<%= request.getAttribute("yearMonth") != null ? request.getAttribute("yearMonth") : thisMonth %>">
                    </div>
                    
                    <button type="submit" class="btn btn-primary">조회</button>
                    <button type="button" class="btn btn-secondary" onclick="location.href='statistics?searchType=monthly'">전체 조회</button>
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
                                <td><%= stat.getDate() %></td>
                                <td class="amount-cell"><%= nf.format(stat.getTotalAmount()) %>원</td>
                                <td><%= stat.getTotalCount() %>대</td>
                                <td><%= nf.format(avgAmount) %>원</td>
                            </tr>
                            <% } %>
                            <tr style="background: #f0f0f0; font-weight: bold;">
                                <td>합계</td>
                                <td class="amount-cell"><%= nf.format(totalMonthlyAmount) %>원</td>
                                <td><%= totalMonthlyCount %>대</td>
                                <td><%= totalMonthlyCount > 0 ? nf.format(totalMonthlyAmount / totalMonthlyCount) : "0" %>원</td>
                            </tr>
                        </tbody>
                    </table>
                <% } else if ("monthly".equals(request.getAttribute("searchType"))) { %>
                    <div class="no-data">해당 월의 데이터가 없습니다.</div>
                <% } %>
            </div>
            
            <!-- 차종별 통계 탭 -->
            <div id="type-tab" class="tab-content">
                <h2 class="section-title">🚗 차종별 이용 통계</h2>
                
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
                                    switch(stat.getKindOfDiscount()) {
                                        case "normal": kindName = "일반"; break;
                                        case "light": kindName = "경차"; break;
                                        case "disabled": kindName = "장애인"; break;
                                        case "monthly": kindName = "월정액"; break;
                                        default: kindName = stat.getKindOfDiscount();
                                    }
                                }
                            %>
                            <tr>
                                <td><strong><%= kindName %></strong></td>
                                <td><%= stat.getTypeCount() %>건</td>
                                <td><%= String.format("%.2f", stat.getTypePercentage()) %>%</td>
                            </tr>
                            <% } %>
                            <tr style="background: #f0f0f0; font-weight: bold;">
                                <td>합계</td>
                                <td><%= totalTypeCount %>건</td>
                                <td>100%</td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <!-- 차트 -->
                    <div class="chart-container">
                        <h3 style="margin-bottom: 20px; color: #333;">차종별 이용 비율 차트</h3>
                        <% 
                        for (StatisticsVO stat : typeStats) { 
                            String barClass = "bar-normal";
                            String kindName = "미분류";
                            if (stat.getKindOfDiscount() != null) {
                                switch(stat.getKindOfDiscount()) {
                                    case "normal": 
                                        kindName = "일반"; 
                                        break;
                                    case "light": 
                                        barClass = "bar-light"; 
                                        kindName = "경차"; 
                                        break;
                                    case "disabled": 
                                        barClass = "bar-disabled"; 
                                        kindName = "장애인"; 
                                        break;
                                    case "monthly": 
                                        barClass = "bar-monthly"; 
                                        kindName = "월정액"; 
                                        break;
                                    default: 
                                        kindName = stat.getKindOfDiscount();
                                }
                            }
                        %>
                        <div class="type-bar">
                            <div class="type-label"><%= kindName %></div>
                            <div class="bar-container">
                                <div class="bar-fill <%= barClass %>" style="width: <%= stat.getTypePercentage() %>%">
                                    <%= String.format("%.1f", stat.getTypePercentage()) %>%
                                </div>
                            </div>
                            <div class="type-count"><%= stat.getTypeCount() %>건</div>
                        </div>
                        <% } %>
                    </div>
                <% } else { %>
                    <div class="no-data">차종별 통계 데이터가 없습니다.</div>
                <% } %>
            </div>
        </div>
        
        <!-- 하단 버튼 -->
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/" class="back-btn">← 메인으로 돌아가기</a>
        </div>
    </div>
    
    <script>
        // 탭 전환 함수
        function showTab(tabName) {
            // 모든 탭 버튼 비활성화
            const tabButtons = document.querySelectorAll('.tab-btn');
            tabButtons.forEach(btn => btn.classList.remove('active'));
            
            // 모든 탭 컨텐츠 숨김
            const tabContents = document.querySelectorAll('.tab-content');
            tabContents.forEach(content => content.style.display = 'none');
            
            // 선택된 탭 활성화
            event.target.classList.add('active');
            document.getElementById(tabName + '-tab').style.display = 'block';
        }
        
        // 페이지 로드 시 검색 타입에 따라 탭 활성화
        window.onload = function() {
            const searchType = '<%= request.getAttribute("searchType") %>';
            if (searchType === 'monthly') {
                showTabByName('monthly');
            } else if (searchType === 'daily') {
                showTabByName('daily');
            }
        };
        
        function showTabByName(tabName) {
            // 모든 탭 버튼 비활성화
            const tabButtons = document.querySelectorAll('.tab-btn');
            tabButtons.forEach(btn => btn.classList.remove('active'));
            
            // 모든 탭 컨텐츠 숨김
            const tabContents = document.querySelectorAll('.tab-content');
            tabContents.forEach(content => content.style.display = 'none');
            
            // 선택된 탭 활성화
            const buttons = Array.from(tabButtons);
            if (tabName === 'daily') {
                buttons[0].classList.add('active');
                document.getElementById('daily-tab').style.display = 'block';
            } else if (tabName === 'monthly') {
                buttons[1].classList.add('active');
                document.getElementById('monthly-tab').style.display = 'block';
            } else if (tabName === 'type') {
                buttons[2].classList.add('active');
                document.getElementById('type-tab').style.display = 'block';
            }
        }
    </script>
</body>
</html>
