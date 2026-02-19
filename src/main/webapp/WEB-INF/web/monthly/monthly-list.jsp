<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.parkinglot.dto.MonthlyParkingDTO" %>
<%
    /* Controller 에서 request 에 담아 보낸 값들을 꺼낸다 */
    List<MonthlyParkingDTO> memberList = (List<MonthlyParkingDTO>) request.getAttribute("memberList");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
    String keyword = (String) request.getAttribute("keyword"); // 검색 키워드 (없으면 null)
    Integer pageSize = (Integer) request.getAttribute("pageSize"); // 페이지당 표시 건수
    String sort = (String) request.getAttribute("sort");     // 정렬 컬럼
    String order = (String) request.getAttribute("order");    // 정렬 방향

    /* null-safe 기본값 처리 */
    if (totalCount == null) totalCount = 0;
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (pageSize == null) pageSize = 10;
    if (sort == null) sort = "begin_date";
    if (order == null) order = "DESC";

    String safeKeyword = (keyword != null) ? keyword : ""; // input value 에 쓸 안전한 값

    // 페이지네이션 링크에 공통으로 붙을 파라미터 (keyword + sort + order + pageSize)
    // 각 파라미터가 기본값이어도 명시적으로 포함해 링크 이동 시 상태 유지
    String baseParams = "sort=" + sort + "&order=" + order + "&pageSize=" + pageSize
            + (keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "");

    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>월정액 회원 관리 - 스마트주차 반월당점</title>
    <link rel="stylesheet" href="<%= contextPath %>/static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <%-- =====================================================================
         아래 스타일은 이 페이지에서만 한 번만 사용하는 요소들임
         ===================================================================== --%>
    <style>
        /* 보드(occupancy-section)를 flex 컬럼으로 만들어
           테이블 영역은 늘어나고 페이지네이션은 항상 하단에 고정되도록 한다. */
        .occupancy-section {
            display: flex;
            flex-direction: column;
        }

        /* 테이블 wrapper: 남은 공간을 모두 차지하고 내부 스크롤 허용 */
        .table-scroll-wrapper {
            flex: 1;
            overflow-y: auto;
            min-height: 0; /* flex 자식의 overflow 버그 방지 */
            max-height: 600px; /* 원하는 최대 높이로 조정 */
            border-radius: 12px;
        }

        /* 스크롤바 스타일 (public.css 의 .table-container 와 동일 규칙 적용) */
        .table-scroll-wrapper::-webkit-scrollbar {
            width: 6px;
        }

        .table-scroll-wrapper::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 10px;
        }

        .table-scroll-wrapper::-webkit-scrollbar-thumb {
            background: var(--primary-color);
            border-radius: 10px;
        }

        .table-scroll-wrapper::-webkit-scrollbar-thumb:hover {
            background: #2e59d9;
        }

        /* 페이지네이션 영역: 보드 안 하단에 위치, 상단 구분선 */
        .pagination-bar {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            padding-top: 18px;
            border-top: 1px solid var(--border-color);
            flex-shrink: 0; /* 크기 고정 – 줄어들지 않음 */
        }

        /* ── 모달 오버레이 ── */
        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0, 0, 0, 0.45);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        /* 모달 박스 */
        .modal-box {
            background: var(--white);
            border-radius: var(--radius-md);
            padding: 36px 40px;
            max-width: 420px;
            width: 90%;
            text-align: center;
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
            animation: fadeIn 0.25s ease;
        }

        .modal-box .modal-icon {
            font-size: 48px;
            margin-bottom: 16px;
        }

        .modal-box .modal-icon.success {
            color: var(--success-color);
        }

        .modal-box .modal-icon.error {
            color: var(--danger-color);
        }

        .modal-box .modal-title {
            font-size: 20px;
            font-weight: bold;
            color: var(--text-dark);
            margin-bottom: 10px;
        }

        .modal-box .modal-message {
            font-size: 15px;
            color: var(--text-gray);
            line-height: 1.6;
            margin-bottom: 24px;
        }

        .modal-box .modal-close-btn {
            width: auto;
            padding: 10px 32px;
            border-radius: 8px;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div class="container">

    <!-- ========== 헤더 ========== -->
    <header class="dashboard-header">
        <%@include file="../common/header.jsp" %>
    </header>

    <!-- ========== 요약 카드 ========== -->
    <div class="cards-container" style="grid-template-columns: repeat(2, 1fr);">
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-users"></i>
                <h2>전체 월정액 회원</h2>
            </div>
            <div class="value"><%= totalCount %>명</div>
            <div class="label">등록된 정기권 회원 수</div>
        </div>
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-book-open"></i>
                <h2>페이지 현황</h2>
            </div>
            <div class="value"><%= currentPage %> / <%= totalPages %>
            </div>
            <div class="label">현재 페이지 / 전체 페이지</div>
        </div>
    </div>

    <!-- ========== 회원 목록 보드 ========== -->
    <div class="occupancy-section" style="margin-top: 20px;">

        <!-- 섹션 헤더 -->
        <div class="section-header">
            <h2 class="section-title" style="margin-bottom: 0; border-bottom: none; padding-bottom: 0;">
                <i class="fas fa-id-card"></i> 월정액 회원 목록
            </h2>
            <div style="display: flex; gap: 10px; align-items: center;">
                <!-- 검색 폼: GET 방식으로 keyword 파라미터 전송, 검색 시 페이지는 1로 초기화
                     현재 정렬/pageSize 상태도 hidden 으로 함께 전달해 상태 유지 -->
                <form method="get" action="<%= contextPath %>/monthly/list"
                      style="display: flex; gap: 8px; align-items: center; margin: 0;">
                    <input type="hidden" name="sort" value="<%= sort %>">
                    <input type="hidden" name="order" value="<%= order %>">
                    <input type="hidden" name="pageSize" value="<%= pageSize %>">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" name="keyword"
                               placeholder="차량번호 · 이름 · 연락처"
                               value="<%= safeKeyword %>"
                               style="width: 180px;">
                    </div>
                    <button type="submit" class="btn-primary" style="width: auto; padding: 8px 16px;">
                        검색
                    </button>
                    <% if (keyword != null) { %>
                    <%-- 검색 중일 때만 '초기화' 버튼 표시 (정렬/pageSize 상태는 유지) --%>
                    <a href="<%= contextPath %>/monthly/list?sort=<%= sort %>&order=<%= order %>&pageSize=<%= pageSize %>">
                        <button type="button" class="btn-secondary" style="width: auto; padding: 8px 14px;">
                            <i class="fas fa-times"></i>
                        </button>
                    </a>
                    <% } %>
                </form>
                <a href="<%= contextPath %>/monthly/register">
                    <button class="btn-primary" style="width: auto; padding: 10px 20px;">
                        <i class="fas fa-user-plus"></i> 신규 등록
                    </button>
                </a>
            </div>
        </div>

        <%-- 페이지당 표시 건수 선택 버튼 + 검색 결과 안내 문구 --%>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <% if (keyword != null) { %>
            <p style="font-size: 13px; color: var(--text-gray); margin: 0 0 0 2px;">
                "<strong><%= safeKeyword %>
            </strong>" 검색 결과 &nbsp;—&nbsp; 총 <strong><%= totalCount %>건</strong>
            </p>
            <% } else { %>
            <span></span>
            <% } %>
            <%-- 페이지당 건수 버튼: 현재 선택된 값은 btn-primary, 나머지는 btn-secondary --%>
            <div style="display: flex; gap: 6px; align-items: center;">
                <span style="font-size: 13px; color: var(--text-gray);">페이지당</span>
                <% int[] pageSizeOptions = {10, 20, 30};
                    for (int ps : pageSizeOptions) { %>
                <a href="<%= contextPath %>/monthly/list?page=1&sort=<%= sort %>&order=<%= order %>&pageSize=<%= ps %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>">
                    <button class="<%= pageSize == ps ? "btn-primary" : "btn-secondary" %>"
                            style="width: auto; padding: 6px 14px; font-size: 13px;"><%= ps %>명
                    </button>
                </a>
                <% } %>
            </div>
        </div>

        <!-- 회원 테이블 (내부 스크롤) -->
        <%-- 정렬 헬퍼 메서드 선언 (JSP 선언 블록 - 클래스 멤버로 생성됨) --%>
        <%!
            // 정렬 아이콘 반환 (현재 정렬 컬럼에만 방향 아이콘 표시)
            private String sortIcon(String col, String currentSort, String currentOrder) {
                if (!col.equals(currentSort)) return "<i class='fas fa-sort' style='opacity:0.3'></i>";
                return "ASC".equals(currentOrder)
                        ? "<i class='fas fa-sort-up'></i>"
                        : "<i class='fas fa-sort-down'></i>";
            }

            // 클릭 시 이동할 order 값 반환 (같은 컬럼이면 토글, 다른 컬럼이면 ASC)
            private String nextOrder(String col, String currentSort, String currentOrder) {
                if (!col.equals(currentSort)) return "ASC";
                return "ASC".equals(currentOrder) ? "DESC" : "ASC";
            }
        %>
        <div class="table-scroll-wrapper">
            <table class="policy-table">
                <thead>
                <tr>
                    <%-- 번호(id) --%>
<%--                    필요 없을 것 같아서 일단 주석 처리--%>
<%--                    <th>--%>
<%--                        <a href="<%= contextPath %>/monthly/list?page=1&sort=id&order=<%= nextOrder("id", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"--%>
<%--                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">--%>
<%--                            번호 <%=sortIcon("id", sort, order)%>--%>
<%--                        </a>--%>
<%--                    </th>--%>
                    <%-- 차량번호 --%>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=plate_number&order=<%= nextOrder("plate_number", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            차량번호 <%=sortIcon("plate_number", sort, order)%>
                        </a>
                    </th>
                    <%-- 이름 --%>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=name&order=<%= nextOrder("name", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            이름 <%=sortIcon("name", sort, order)%>
                        </a>
                    </th>
                    <%-- 연락처 --%>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=phone_number&order=<%= nextOrder("phone_number", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            연락처 <%=sortIcon("phone_number", sort, order)%>
                        </a>
                    </th>
                    <%-- 시작일 --%>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=begin_date&order=<%= nextOrder("begin_date", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            시작일 <%=sortIcon("begin_date", sort, order)%>
                        </a>
                    </th>
                    <%-- 만료일 --%>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=expiry_date&order=<%= nextOrder("expiry_date", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            만료일 <%=sortIcon("expiry_date", sort, order)%>
                        </a>
                    </th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (memberList != null && !memberList.isEmpty()) {
                        for (MonthlyParkingDTO member : memberList) {
                %>
                <tr>
<%--                    id 필요 없을 것 같아서 일단 주석 처리 --%>
<%--                    <td><%= member.getId() %>--%>
<%--                    </td>--%>
                    <td><strong><%= member.getPlateNumber() %>
                    </strong></td>
                    <td><%= member.getName() %>
                    </td>
                    <td><%= member.getPhoneNumber() %>
                    </td>
                    <td class="date-cell"><%= member.getBeginDate() %>
                    </td>
                    <td class="date-cell"><%= member.getExpiryDate() %>
                    </td>
                    <td>
                        <div style="display: flex; gap: 8px; justify-content: center;">
                            <!-- 수정 버튼 -->
                            <a href="<%= contextPath %>/monthly/edit?id=<%= member.getId() %>">
                                <button class="btn-info"
                                        style="width: auto; padding: 6px 14px; font-size: 13px;">
                                    <i class="fas fa-edit"></i> 수정
                                </button>
                            </a>
                            <!-- 삭제 버튼: confirm 대신 커스텀 모달로 처리 -->
                            <button class="btn-danger"
                                    style="width: auto; padding: 6px 14px; font-size: 13px;"
                                    onclick="openDeleteConfirm(<%= member.getId() %>, '<%= member.getName() %>', '<%= member.getPlateNumber() %>')">
                                <i class="fas fa-trash"></i> 삭제
                            </button>
                        </div>
                    </td>
                </tr>
                <%
                    } // end for
                } else {
                %>
                <tr>
                    <td colspan="7">
                        <div class="no-data">
                            <i class="fas fa-users-slash"></i>
                            <p>등록된 월정액 회원이 없습니다.</p>
                        </div>
                    </td>
                </tr>
                <%
                    } // end if-else
                %>
                </tbody>
            </table>
        </div><!-- /table-scroll-wrapper -->

        <!-- ========== 페이지네이션 (보드 하단 고정) ========== -->
        <% if (totalPages > 1) { %>
        <div class="pagination-bar">

            <%
                // baseParams 는 상단 스크립틀릿에서 이미 선언됨 (sort + order + pageSize + keyword 포함)
            %>

            <%-- 이전 버튼 --%>
            <% if (currentPage > 1) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= currentPage - 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 16px;">
                    <i class="fas fa-chevron-left"></i>
                </button>
            </a>
            <% } else { %>
            <button class="btn-secondary" style="width: auto; padding: 8px 16px; opacity: 0.4;" disabled>
                <i class="fas fa-chevron-left"></i>
            </button>
            <% } %>

            <%-- 페이지 번호: 한 블록에 최대 5페이지씩 표시 --%>
            <%
                int blockSize = 5;
                int blockStart = ((currentPage - 1) / blockSize) * blockSize + 1;
                int blockEnd = Math.min(blockStart + blockSize - 1, totalPages);
            %>

            <%-- 이전 블록 점프 버튼 (1번 블록이면 숨김) --%>
            <% if (blockStart > 1) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= blockStart - 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 12px; font-size: 13px;">···</button>
            </a>
            <% } %>

            <%-- 현재 블록의 페이지 번호 출력 --%>
            <% for (int i = blockStart; i <= blockEnd; i++) { %>
            <% if (i == currentPage) { %>
            <button class="btn-primary"
                    style="width: auto; padding: 8px 14px; min-width: 40px;"><%= i %>
            </button>
            <% } else { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= i %>&<%= baseParams %>">
                <button class="btn-secondary"
                        style="width: auto; padding: 8px 14px; min-width: 40px;"><%= i %>
                </button>
            </a>
            <% } %>
            <% } %>

            <%-- 다음 블록 점프 버튼 (마지막 블록이면 숨김) --%>
            <% if (blockEnd < totalPages) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= blockEnd + 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 12px; font-size: 13px;">···</button>
            </a>
            <% } %>

            <%-- 다음 버튼 --%>
            <% if (currentPage < totalPages) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= currentPage + 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 16px;">
                    <i class="fas fa-chevron-right"></i>
                </button>
            </a>
            <% } else { %>
            <button class="btn-secondary" style="width: auto; padding: 8px 16px; opacity: 0.4;" disabled>
                <i class="fas fa-chevron-right"></i>
            </button>
            <% } %>

        </div><!-- /pagination-bar -->
        <% } %>

    </div><!-- /occupancy-section -->
</div><!-- /container -->


<!-- =====================================================================
     ① 처리 결과 알림 모달 (message / error)
     ===================================================================== -->
<div class="modal-overlay" id="alertModal">
    <div class="modal-box">
        <div class="modal-icon" id="alertModalIcon"></div>
        <div class="modal-title" id="alertModalTitle"></div>
        <div class="modal-message" id="alertModalMessage"></div>
        <button class="btn-primary modal-close-btn" onclick="closeAlertModal()">확인</button>
    </div>
</div>

<!-- =====================================================================
     ② 삭제 확인 모달
     ===================================================================== -->
<div class="modal-overlay" id="deleteConfirmModal">
    <div class="modal-box">
        <div class="modal-icon error"><i class="fa-solid fa-triangle-exclamation"></i></div>
        <div class="modal-title">회원 삭제</div>
        <div class="modal-message" id="deleteConfirmMessage"></div>
        <div style="display: flex; gap: 12px; justify-content: center;">
            <button class="btn-secondary modal-close-btn" style="background:#6c757d;"
                    onclick="closeDeleteConfirm()">취소
            </button>
            <button class="btn-danger modal-close-btn" id="deleteConfirmBtn">
                <i class="fas fa-trash"></i> 삭제
            </button>
        </div>
    </div>
</div>

<!-- 삭제 실행용 hidden form -->
<form id="deleteForm" action="<%= contextPath %>/monthly/delete" method="post">
    <input type="hidden" name="id" id="deleteTargetId">
</form>


<script>
    /* ──────────────────────────────────────────────
       ① 처리 결과 알림 모달
    ────────────────────────────────────────────── */
    (function () {
        /* 서버에서 전달된 message / error 값을 JSP 스크립틀릿으로 주입 */
        const message = "<%= message != null ? message : "" %>";
        const error = "<%= error   != null ? error   : "" %>";

        function resolveMessage(val) {
            if (val === "registered") return "회원이 성공적으로 등록되었습니다.";
            if (val === "updated") return "회원 정보가 수정되었습니다.";
            if (val === "deleted") return "회원이 삭제되었습니다.";
            return val;
        }

        function resolveError(val) {
            if (val === "notfound") return "해당 회원을 찾을 수 없습니다.";
            return val;
        }

        if (message) {
            document.getElementById("alertModalIcon").innerHTML = '<i class="fa-solid fa-circle-check success"></i>';
            document.getElementById("alertModalIcon").className = "modal-icon success";
            document.getElementById("alertModalTitle").textContent = "처리 완료";
            document.getElementById("alertModalMessage").textContent = resolveMessage(message);
            document.getElementById("alertModal").classList.add("active");
        } else if (error) {
            document.getElementById("alertModalIcon").innerHTML = '<i class="fa-solid fa-circle-exclamation"></i>';
            document.getElementById("alertModalIcon").className = "modal-icon error";
            document.getElementById("alertModalTitle").textContent = "오류 발생";
            document.getElementById("alertModalMessage").textContent = resolveError(error);
            document.getElementById("alertModal").classList.add("active");
        }
    })();

    function closeAlertModal() {
        document.getElementById("alertModal").classList.remove("active");
    }

    /* ──────────────────────────────────────────────
       ② 삭제 확인 모달
    ────────────────────────────────────────────── */
    function openDeleteConfirm(id, name, plateNumber) {
        document.getElementById("deleteConfirmMessage").textContent =
            name + "(" + plateNumber + ") 회원을 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.";
        document.getElementById("deleteTargetId").value = id;
        document.getElementById("deleteConfirmModal").classList.add("active");
    }

    function closeDeleteConfirm() {
        document.getElementById("deleteConfirmModal").classList.remove("active");
    }

    /* 삭제 확인 버튼 클릭 시 form 제출 */
    document.getElementById("deleteConfirmBtn").addEventListener("click", function () {
        document.getElementById("deleteForm").submit();
    });

    /* 오버레이 클릭 시 모달 닫기 */
    document.getElementById("alertModal").addEventListener("click", function (e) {
        if (e.target === this) closeAlertModal();
    });
    document.getElementById("deleteConfirmModal").addEventListener("click", function (e) {
        if (e.target === this) closeDeleteConfirm();
    });
</script>

</body>
</html>
