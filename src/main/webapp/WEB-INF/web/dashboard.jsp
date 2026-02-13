<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>스마트주차 반월당점 - 주차장 대시보드</title>

    <!-- CSS -->
    <link rel="stylesheet" href="./static/css/public.css">
    <!-- Font Awesome (아이콘) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

</head>
<body>

    <!-- 컨텍스트 경로를 JavaScript에 전달 -->
<%--    <input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">--%>

    <!-- 로딩 인디케이터 -->
    <div id="loadingIndicator" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%,-50%); z-index:9999;">
        <i class="fas fa-spinner fa-spin fa-3x"></i>
        <p>데이터 로딩 중...</p>
    </div>

    <div class="container">

        <!-- ========== 헤더 영역 ========== -->
        <header class="dashboard-header">
            <%@include file="./common/header.jsp" %>
        </header>

        <!-- ========== 메인 컨텐츠 ========== -->
        <div class="main-content">
            <!-- 좌측 패널: 통계 + 주차 현황 -->
            <div class="left-panel">

                <!-- 통계 카드 -->
                <div class="stats-container">
                    <!-- 현재 주차 대수 -->
                    <div class="card">
                        <div class="value" id="currentParkedCount">0</div>
                        <div class="label">현재 주차 대수</div>
                    </div>

                    <!-- 주차 가능 대수 -->
                    <div class="card">
                        <div class="value" id="availableSpots">20</div>
                        <div class="label">주차 가능 대수</div>
                    </div>

                    <!-- 오늘 방문자 수 -->
                    <div class="card">
                        <div class="value" id="todayVisitorCount">0</div>
                        <div class="label">오늘 방문자 수</div>
                    </div>

                    <!-- 점유율 -->
                    <div class="card" id="occupancyCard">
                        <div class="value" id="occupancyRate">0%</div>
                        <div class="label">점유율</div>
                    </div>
                </div>

                <!-- 주차 현황판 -->
                <div class="occupancy-section">
                    <div class="section-header">
                        <h2>주차 현황</h2>

                        <!-- 검색창 -->
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text"
                                   id="searchInput"
                                   placeholder="차량번호 검색"
                                   maxlength="20">
                            <button id="searchBtn" style="border:none; background:none; cursor:pointer;">
                                <i class="fas fa-arrow-right"></i>
                            </button>
                            <button id="clearSearchBtn" style="border:none; background:none; cursor:pointer;" title="검색 초기화">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    </div>

                    <!-- A구역 (A01 ~ A10) -->
                    <div class="row-label">A구역 (1-10번)</div>
                    <div class="parking-row" id="parkingRowA">
                        <!-- JavaScript로 동적 생성 -->
                    </div>

                    <!-- B구역 (A11 ~ A20) -->
                    <div class="row-label">A구역 (11-20번)</div>
                    <div class="parking-row" id="parkingRowB">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
            </div>

            <!-- 우측 패널: 상세 정보 -->
            <div class="right-panel">
                <div class="detail-placeholder" id="detailPanel">
                    <i class="fas fa-info-circle"></i>
                    <p>주차 구역을 클릭하면<br>상세 정보가 표시됩니다</p>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="./static/js/dashboard.js"></script>

</body>
</html>
