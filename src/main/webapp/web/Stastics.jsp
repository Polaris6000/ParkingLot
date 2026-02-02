<%--
  Created by IntelliJ IDEA.
  User: warmice8226
  Date: 26. 1. 14.
  Time: 오후 5:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="../static/css/Statstics.css">

</head>
<body>
<main>
    <div>
        <div class="top-section">
            <%-- 주은씨 위쪽 구역 --%>
            <div class="statistics-header">
                <h1>Statistics</h1>
                <div class="header-buttons">
                    <button class="notification-btn">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 22C13.1 22 14 21.1 14 20H10C10 21.1 10.9 22 12 22ZM18 16V11C18 7.93 16.37 5.36 13.5 4.68V4C13.5 3.17 12.83 2.5 12 2.5C11.17 2.5 10.5 3.17 10.5 4V4.68C7.64 5.36 6 7.92 6 11V16L4 18V19H20V18L18 16Z" fill="#6366F1"/>
                        </svg>
                    </button>
                    <button class="export-btn">Export Data</button>
                </div>
            </div>
            <div class="statistics-summary">
                <div class="summary-card">
                    <div class="summary-value revenue">0 ₩</div>
                    <div class="summary-label">Total Revenue</div>
                </div>
                <div class="summary-card">
                    <div class="summary-value count">0</div>
                    <div class="summary-label">Total Count</div>
                </div>
                <div class="summary-card">
                    <div class="summary-value period">0</div>
                    <div class="summary-label">Period</div>
                </div>
            </div>
        </div>

        <div class="bottom-section">
        </div>
        <div>
            <%-- 혜윰 아래쪽 구역 --%>
        </div>
</main>
</body>
</html>