<%@ page import="com.example.parkinglot.dto.ParkingCarDTO" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>스마트주차 반월당점 - 주차장 대시보드</title>

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">

    <!-- Font Awesome (아이콘) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- JavaScript -->
    <script defer src="${pageContext.request.contextPath}/static/js/dashboard.js"></script>
</head>
<body>

<div style="display: none" id="jsonData-car">
    ${carData}
</div>
<div style="display: none" id="jsonData-fee">
    ${feeData}
</div>

<!-- 컨텍스트 경로를 JavaScript에 전달 -->
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">

<!-- 로딩 인디케이터 -->
<div id="loadingIndicator"
     style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%,-50%); z-index:9999; text-align: center">
    <i class="fas fa-spinner fa-spin fa-3x"></i>
    <br>
    <br>
    <p>데이터 로딩 중...</p>
</div>

<div class="container">

    <header class="dashboard-header">
        <%@include file="common/header.jsp" %>
    </header>

    <!-- ========== 메인 컨텐츠 ========== -->
    <div class="main-content">
        <!-- 좌측 패널: 통계 + 주차 현황 -->
        <div class="left-panel">

            <!-- 통계 카드 -->
            <div class="stats-container">
                <!-- 현재 주차 대수 -->
                <div class="card">
                    <div class="value" id="currentParkedCount">${currentParking}</div>
                    <div class="label">현재 주차 대수</div>
                </div>

                <!-- 주차 가능 대수 -->
                <div class="card">
                    <div class="value" id="availableSpots">${20 - currentParking}</div>
                    <div class="label">주차 가능 대수</div>
                </div>

                <!-- 금일 방문자 수 -->
                <div class="card">
                    <div class="value" id="todayVisitorCount">${todayVisitor}</div>
                    <div class="label">금일 방문자 수</div>
                </div>

                <!-- 점유율 -->
                <div class="card" id="occupancyCard">
                    <div class="value" id="occupancyRate">${currentParking * 5}%</div>
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
                        <button id="clearSearchBtn" style="border:none; background:none; cursor:pointer;"
                                title="검색 초기화">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>

                <!-- A구역 (A01 ~ A10) -->
                <div class="row-label">A구역 (1-10번)</div>
                <div class="parking-row" id="parkingRowA">
                    <div class="spot" data-spot="A01" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A01</div>
                    </div>
                    <div class="spot" data-spot="A02" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A02</div>
                    </div>
                    <div class="spot" data-spot="A03" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A03</div>
                    </div>
                    <div class="spot" data-spot="A04" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A04</div>
                    </div>
                    <div class="spot" data-spot="A05" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A05</div>
                    </div>
                    <div class="spot" data-spot="A06" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A06</div>
                    </div>
                    <div class="spot" data-spot="A07" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A07</div>
                    </div>
                    <div class="spot" data-spot="A08" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A08</div>
                    </div>
                    <div class="spot" data-spot="A09" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A09</div>
                    </div>
                    <div class="spot" data-spot="A10" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A10</div>
                    </div>
                </div>

                <!-- B구역 (A11 ~ A20) -->
                <div class="row-label">A구역 (11-20번)</div>
                <div class="parking-row" id="parkingRowB">
                    <div class="spot" data-spot="A11" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A11</div>
                    </div>
                    <div class="spot" data-spot="A12" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A12</div>
                    </div>
                    <div class="spot" data-spot="A13" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A13</div>
                    </div>
                    <div class="spot" data-spot="A14" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A14</div>
                    </div>
                    <div class="spot" data-spot="A15" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A15</div>
                    </div>
                    <div class="spot" data-spot="A16" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A16</div>
                    </div>
                    <div class="spot" data-spot="A17" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A17</div>
                    </div>
                    <div class="spot" data-spot="A18" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A18</div>
                    </div>
                    <div class="spot" data-spot="A19" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A19</div>
                    </div>
                    <div class="spot" data-spot="A20" title="사용 가능">
                        <i class="fas fa-car"></i>
                        <div class="spot-label">A20</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 우측 패널: 상세 정보 -->
        <div class="right-panel">
            <%--                <div class="detail-placeholder" id="detailPanel">--%>
            <%--                    <i class="fas fa-info-circle"></i>--%>
            <%--                    <p>주차 구역을 클릭하면<br>상세 정보가 표시됩니다</p>--%>
            <%--                </div>--%>
            <%@include file="common/carInfo.jsp" %>
            <div class="info-row">
                <!-- 정산 버튼 -->
                <button id="execute-btn" class="exit-btn" style="background: gray; cursor: not-allowed;" disabled>
                    <i class="fas fa-sign-out-alt"></i> <span>정산하기</span>
                </button>
            </div>
        </div>
    </div>
</div>

<%--모달창 제어를 위한 공간--%>
<div class="modal">
    <div class="modal-content">
        <%@include file="common/carInfo.jsp" %>
        <button>결제</button>
        <button>취소</button>
    </div>
</div>

</body>
</html>
