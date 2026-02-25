<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.parkinglot.dto.MonthlyParkingDTO" %>
<%
    /* Controller에서 request에 담아 보낸 값들을 꺼낸다 */
    List<MonthlyParkingDTO> memberList = (List<MonthlyParkingDTO>) request.getAttribute("memberList");
    Integer totalCount  = (Integer) request.getAttribute("totalCount");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages  = (Integer) request.getAttribute("totalPages");
    String message  = (String) request.getAttribute("message");
    String error    = (String) request.getAttribute("error");
    String keyword  = (String) request.getAttribute("keyword");
    Integer pageSize = (Integer) request.getAttribute("pageSize");
    String sort  = (String) request.getAttribute("sort");
    String order = (String) request.getAttribute("order");

    /* null-safe 기본값 처리 */
    if (totalCount  == null) totalCount  = 0;
    if (currentPage == null) currentPage = 1;
    if (totalPages  == null) totalPages  = 1;
    if (pageSize    == null) pageSize    = 10;
    if (sort  == null) sort  = "begin_date";
    if (order == null) order = "DESC";

    String safeKeyword = (keyword != null) ? keyword : "";

    /* 페이지네이션 링크에 공통으로 붙을 파라미터 */
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

    <%-- 이 페이지에서만 한 번 사용하는 스타일 --%>
    <style>
        .occupancy-section {
            display: flex;
            flex-direction: column;
        }
        .table-scroll-wrapper {
            flex: 1;
            overflow-y: auto;
            min-height: 0;
            max-height: 600px;
            border-radius: 12px;
        }
        .table-scroll-wrapper::-webkit-scrollbar       { width: 6px; }
        .table-scroll-wrapper::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 10px; }
        .table-scroll-wrapper::-webkit-scrollbar-thumb { background: var(--primary-color); border-radius: 10px; }
        .table-scroll-wrapper::-webkit-scrollbar-thumb:hover { background: #2e59d9; }
        .pagination-bar {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            padding-top: 18px;
            border-top: 1px solid var(--border-color);
            flex-shrink: 0;
        }

        /* 상태 뱃지 */
        .badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
        }
        .badge-active    { background: #d4edda; color: #155724; }
        .badge-expired   { background: #f8d7da; color: #721c24; }
        .badge-scheduled { background: #fff3cd; color: #856404; }

        /* 모달 */
        .modal-overlay { display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 1000; justify-content: center; align-items: center; }
        .modal-overlay.active { display: flex; }
        .modal-box { background: var(--white); border-radius: var(--radius-md); padding: 36px 40px; max-width: 420px; width: 90%; text-align: center; box-shadow: 0 12px 40px rgba(0,0,0,0.18); }
        .modal-box .modal-icon { font-size: 48px; margin-bottom: 16px; }
        .modal-box .modal-icon.success { color: var(--success-color); }
        .modal-box .modal-icon.error   { color: var(--danger-color); }
        .modal-box .modal-title   { font-size: 20px; font-weight: bold; color: var(--text-dark); margin-bottom: 10px; }
        .modal-box .modal-message { font-size: 15px; color: var(--text-gray); line-height: 1.6; margin-bottom: 24px; }
        .modal-box .modal-close-btn { width: auto; padding: 10px 32px; border-radius: 8px; font-size: 15px; }
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
            <div class="value"><%= currentPage %> / <%= totalPages %></div>
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
                <form method="get" action="<%= contextPath %>/monthly/list"
                      style="display: flex; gap: 8px; align-items: center; margin: 0;">
                    <input type="hidden" name="sort"     value="<%= sort %>">
                    <input type="hidden" name="order"    value="<%= order %>">
                    <input type="hidden" name="pageSize" value="<%= pageSize %>">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" name="keyword"
                               placeholder="차량번호 · 이름 · 연락처"
                               value="<%= safeKeyword %>"
                               style="width: 180px;">
                    </div>
                    <button type="submit" class="btn-primary" style="width: auto; padding: 8px 16px;">검색</button>
                    <% if (keyword != null) { %>
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

        <!-- 페이지당 건수 + 검색 결과 안내 -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <% if (keyword != null) { %>
            <p style="font-size: 13px; color: var(--text-gray); margin: 0 0 0 2px;">
                "<strong><%= safeKeyword %></strong>" 검색 결과 &nbsp;—&nbsp; 총 <strong><%= totalCount %>건</strong>
            </p>
            <% } else { %>
            <span></span>
            <% } %>
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

        <!-- 회원 테이블 -->
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

            // 상태 코드 → 한글 뱃지 HTML 반환
            private String statusBadge(String status) {
                if ("active".equals(status))    return "<span class='badge badge-active'>이용중</span>";
                if ("expired".equals(status))   return "<span class='badge badge-expired'>만료</span>";
                if ("scheduled".equals(status)) return "<span class='badge badge-scheduled'>예약</span>";
                return "<span class='badge'>-</span>";
            }
        %>
        <div class="table-scroll-wrapper">
            <table class="policy-table">
                <thead>
                <tr>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=plate_number&order=<%= nextOrder("plate_number", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            차량번호 <%=sortIcon("plate_number", sort, order)%>
                        </a>
                    </th>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=name&order=<%= nextOrder("name", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            이름 <%=sortIcon("name", sort, order)%>
                        </a>
                    </th>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=phone_number&order=<%= nextOrder("phone_number", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            연락처 <%=sortIcon("phone_number", sort, order)%>
                        </a>
                    </th>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=begin_date&order=<%= nextOrder("begin_date", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            시작일 <%=sortIcon("begin_date", sort, order)%>
                        </a>
                    </th>
                    <th>
                        <a href="<%= contextPath %>/monthly/list?page=1&sort=expiry_date&order=<%= nextOrder("expiry_date", sort, order) %>&pageSize=<%= pageSize %><%= keyword != null ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "" %>"
                           style="color:inherit; display:flex; align-items:center; justify-content:center; gap:4px;">
                            만료일 <%=sortIcon("expiry_date", sort, order)%>
                        </a>
                    </th>
                    <%-- 상태: DB CASE WHEN으로 계산된 status 값 표시 --%>
                    <th>상태</th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (memberList != null && !memberList.isEmpty()) {
                        for (MonthlyParkingDTO member : memberList) {
                %>
                <tr>
                    <td><strong><%= member.getPlateNumber() %></strong></td>
                    <td><%= member.getName() %></td>
                    <td><%= member.getPhoneNumber() %></td>
                    <td class="date-cell"><%= member.getBeginDate() %></td>
                    <td class="date-cell"><%= member.getExpiryDate() %></td>
                    <%-- 상태 뱃지: active(이용중) / expired(만료) / scheduled(예약) --%>
                    <td><%=statusBadge(member.getStatus())%></td>
                    <td>
                        <div style="display: flex; gap: 8px; justify-content: center;">
                            <a href="<%= contextPath %>/monthly/edit?id=<%= member.getId() %>">
                                <button class="btn-info" style="width: auto; padding: 6px 14px; font-size: 13px;">
                                    <i class="fas fa-edit"></i> 수정
                                </button>
                            </a>
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

        <!-- ========== 페이지네이션 ========== -->
        <% if (totalPages > 1) { %>
        <div class="pagination-bar">
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

            <%
                int blockSize  = 5;
                int blockStart = ((currentPage - 1) / blockSize) * blockSize + 1;
                int blockEnd   = Math.min(blockStart + blockSize - 1, totalPages);
            %>
            <% if (blockStart > 1) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= blockStart - 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 12px; font-size: 13px;">···</button>
            </a>
            <% } %>

            <% for (int i = blockStart; i <= blockEnd; i++) { %>
            <% if (i == currentPage) { %>
            <button class="btn-primary" style="width: auto; padding: 8px 14px; min-width: 40px;"><%= i %></button>
            <% } else { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= i %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 14px; min-width: 40px;"><%= i %></button>
            </a>
            <% } %>
            <% } %>

            <% if (blockEnd < totalPages) { %>
            <a href="<%= contextPath %>/monthly/list?page=<%= blockEnd + 1 %>&<%= baseParams %>">
                <button class="btn-secondary" style="width: auto; padding: 8px 12px; font-size: 13px;">···</button>
            </a>
            <% } %>

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
        </div>
        <% } %>

    </div><!-- /occupancy-section -->
</div><!-- /container -->


<!-- ① 처리 결과 알림 모달 -->
<div class="modal-overlay" id="alertModal">
    <div class="modal-box">
        <div class="modal-icon" id="alertModalIcon"></div>
        <div class="modal-title" id="alertModalTitle"></div>
        <div class="modal-message" id="alertModalMessage"></div>
        <button class="btn-primary modal-close-btn" onclick="closeAlertModal()">확인</button>
    </div>
</div>

<!-- ② 삭제 확인 모달 -->
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
    /* ① 처리 결과 알림 모달 */
    (function () {
        const message = "<%= message != null ? message : "" %>";
        const error   = "<%= error   != null ? error   : "" %>";

        function resolveMessage(val) {
            if (val === "registered") return "회원이 성공적으로 등록되었습니다.";
            if (val === "updated")    return "회원 정보가 수정되었습니다.";
            if (val === "deleted")    return "회원이 삭제되었습니다.";
            return val;
        }
        function resolveError(val) {
            if (val === "notfound") return "해당 회원을 찾을 수 없습니다.";
            return val;
        }

        if (message) {
            document.getElementById("alertModalIcon").innerHTML  = '<i class="fa-solid fa-circle-check"></i>';
            document.getElementById("alertModalIcon").className  = "modal-icon success";
            document.getElementById("alertModalTitle").textContent   = "처리 완료";
            document.getElementById("alertModalMessage").textContent = resolveMessage(message);
            document.getElementById("alertModal").classList.add("active");
        } else if (error) {
            document.getElementById("alertModalIcon").innerHTML  = '<i class="fa-solid fa-circle-exclamation"></i>';
            document.getElementById("alertModalIcon").className  = "modal-icon error";
            document.getElementById("alertModalTitle").textContent   = "오류 발생";
            document.getElementById("alertModalMessage").textContent = resolveError(error);
            document.getElementById("alertModal").classList.add("active");
        }
    })();

    function closeAlertModal() {
        document.getElementById("alertModal").classList.remove("active");
    }

    /* ② 삭제 확인 모달 */
    function openDeleteConfirm(id, name, plateNumber) {
        document.getElementById("deleteConfirmMessage").textContent =
            name + "(" + plateNumber + ") 회원을 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.";
        document.getElementById("deleteTargetId").value = id;
        document.getElementById("deleteConfirmModal").classList.add("active");
    }
    function closeDeleteConfirm() {
        document.getElementById("deleteConfirmModal").classList.remove("active");
    }

    document.getElementById("deleteConfirmBtn").addEventListener("click", function () {
        document.getElementById("deleteForm").submit();
    });
    document.getElementById("alertModal").addEventListener("click", function (e) {
        if (e.target === this) closeAlertModal();
    });
    document.getElementById("deleteConfirmModal").addEventListener("click", function (e) {
        if (e.target === this) closeDeleteConfirm();
    });
</script>

</body>
</html>