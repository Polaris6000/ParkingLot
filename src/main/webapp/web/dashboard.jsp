<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주차 관리 시스템</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/dashboard.css">
</head>
<body>

<%-- header include (선택사항) --%>
<c:catch var="headerError">
    <%@include file="common/header.jsp" %>
</c:catch>

<div class="container">
    <header class="dashboard-header">
        <h1>주차 관리 시스템</h1>
        <div class="header-buttons">
            <button class="icon-btn"><i class="fa-solid fa-bell"></i></button>
            <button class="exit-btn">차량 출고</button>
        </div>
    </header>

    <main class="main-content">

        <!-- 좌측 패널: 통계 및 주차 현황 -->
        <section class="left-panel">
            <!-- 통계 카드 -->
            <div class="stats-container">
                <div class="card mini">
                    <div class="value" id="occupancy-rate">
                        <c:choose>
                            <c:when test="${stats != null}">
                                ${stats.currentParkedCount} / ${stats.totalSpots}
                            </c:when>
                            <c:otherwise>
                                0 / 20
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="label">현재 주차 점유</div>
                </div>
                <div class="card mini">
                    <div class="value" id="ev-rate">0 / 2</div>
                    <div class="label">전기차 충전 점유</div>
                </div>
                <div class="card full">
                    <div class="value">
                        <c:choose>
                            <c:when test="${stats != null}">
                                ${stats.todayVisitorCount}
                            </c:when>
                            <c:otherwise>
                                0
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="label">오늘 총 방문자 수</div>
                </div>
            </div>

            <!-- 실시간 주차 현황 -->
            <div class="occupancy-section">
                <div class="section-header">
                    <h2>실시간 주차 현황</h2>
                    <div class="search-box">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <label for="searchInput"></label>
                        <input type="text" id="searchInput" placeholder="차량번호 검색 (뒷 4자리)">
                    </div>
                </div>

                <!-- 검색 결과 메시지 -->
                <c:if test="${not empty searchResult}">
                    <div style="background: #e7f5ff; padding: 10px; border-radius: 8px; margin-bottom: 15px;">
                        <strong>${searchResult.plateNumber}</strong>가
                        <strong style="color: var(--accent-blue);">${searchResult.spotNumber}</strong>에 주차되어 있습니다.
                    </div>
                </c:if>
                <c:if test="${not empty searchMessage}">
                    <div style="background: #fff3cd; padding: 10px; border-radius: 8px; margin-bottom: 15px;">
                            ${searchMessage}
                    </div>
                </c:if>

                <!-- 주차 구역 그리드 -->
                <div class="row-group">
                    <div class="row-label">1번 구역 (Row 1)</div>
                    <div id="row1" class="parking-row"></div>
                </div>

                <div class="row-group">
                    <div class="row-label">2번 구역 (Row 2)</div>
                    <div id="row2" class="parking-row"></div>
                </div>
            </div>
        </section>

        <!-- 우측 패널: 상세 정보 -->
        <!-- 우측 패널: 상세 정보 종현님 구역 -->
        <section class="right-panel">
            <div class="detail-placeholder">
                <i class="fa-solid fa-circle-info"></i>
                <p>상세 정보를 보려면<br>주차 칸을 선택하세요.</p>
            </div>
        </section>

    </main>
</div>

<!-- JavaScript 데이터 전달 -->
<script>
    // 주차 구역 데이터를 JavaScript 배열로 변환
    let parkingSpotsData = [
        <c:if test="${parkingSpots != null && parkingSpots.size() > 0}">
            <c:forEach var="spot" items="${parkingSpots}" varStatus="status">
        {
            spotNumber: "${spot.spotNumber}",
            occupied: ${spot.occupied},
            plateNumber: <c:choose><c:when test="${spot.plateNumber != null}">"${spot.plateNumber}"</c:when><c:otherwise>null</c:otherwise></c:choose>,
            entryTime: <c:choose><c:when test="${spot.entryTime != null}">"${spot.entryTime}"</c:when><c:otherwise>null</c:otherwise></c:choose>,
            carId: <c:choose><c:when test="${spot.carId != null}">${spot.carId}</c:when><c:otherwise>null</c:otherwise></c:choose>
        }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        </c:if>
    ];

    // 검색 결과 데이터 전달
    <c:if test="${not empty searchResult}">
    let searchResultSpot = "${searchResult.spotNumber}";
    </c:if>

    // 디버깅 정보
    console.log("주차 구역 데이터 개수:", parkingSpotsData.length);
    console.log("주차 구역 데이터:", parkingSpotsData);
    <c:if test="${not empty searchResult}">
    console.log("검색 결과:", searchResultSpot);
    </c:if>
</script>

<!-- 외부 JavaScript 파일 로드 -->
<script src="${pageContext.request.contextPath}/static/js/dashboard.js"></script>

</body>
</html>
