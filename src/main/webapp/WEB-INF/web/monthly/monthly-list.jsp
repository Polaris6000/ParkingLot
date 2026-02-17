<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>월정액 회원 관리 - 스마트주차 반월당점</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">

    <!-- ========== 헤더 ========== -->
    <header class="dashboard-header">
        <%@include file="../common/header.jsp" %>
    </header>

    <!-- ========== 알림 메시지 ========== -->
    <c:if test="${not empty message}">
        <div class="alert alert-success">
            <i class="fa-solid fa-circle-check"></i>
            <c:choose>
                <c:when test="${message == 'registered'}">회원이 성공적으로 등록되었습니다.</c:when>
                <c:when test="${message == 'updated'}">회원 정보가 수정되었습니다.</c:when>
                <c:when test="${message == 'deleted'}">회원이 삭제되었습니다.</c:when>
                <c:otherwise>${message}</c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <c:choose>
                <c:when test="${error == 'notfound'}">해당 회원을 찾을 수 없습니다.</c:when>
                <c:otherwise>${error}</c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- ========== 요약 카드 ========== -->
    <div class="cards-container" style="grid-template-columns: repeat(2, 1fr);">
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-users"></i>
                <h2>전체 월정액 회원</h2>
            </div>
            <div class="value">${totalCount}명</div>
            <div class="label">등록된 정기권 회원 수</div>
        </div>
        <div class="card card-statistics">
            <div class="card-header">
                <i class="fas fa-book-open"></i>
                <h2>페이지 현황</h2>
            </div>
            <div class="value">${currentPage} / ${totalPages}</div>
            <div class="label">현재 페이지 / 전체 페이지</div>
        </div>
    </div>

    <!-- ========== 회원 목록 섹션 ========== -->
    <div class="occupancy-section" style="margin-top: 20px;">

        <!-- 섹션 헤더 -->
        <div class="section-header">
            <h2 class="section-title" style="margin-bottom: 0; border-bottom: none; padding-bottom: 0;">
                <i class="fas fa-id-card"></i> 월정액 회원 목록
            </h2>
            <a href="${pageContext.request.contextPath}/monthly/register">
                <button class="btn-primary" style="width: auto; padding: 10px 20px;">
                    <i class="fas fa-user-plus"></i> 신규 등록
                </button>
            </a>
        </div>

        <!-- 회원 테이블 -->
        <div class="table-container">
            <table class="policy-table">
                <thead>
                <tr>
                    <th>번호</th>
                    <th>차량번호</th>
                    <th>이름</th>
                    <th>연락처</th>
                    <th>시작일</th>
                    <th>만료일</th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty memberList}">
                        <c:forEach var="member" items="${memberList}">
                            <tr>
                                <td>${member.id}</td>
                                <td><strong>${member.plateNumber}</strong></td>
                                <td>${member.name}</td>
                                <td>${member.phoneNumber}</td>
                                <td class="date-cell">${member.beginDate}</td>
                                <td class="date-cell">${member.expiryDate}</td>
                                <td>
                                    <div style="display: flex; gap: 8px; justify-content: center;">
                                        <!-- 수정 버튼 -->
                                        <a href="${pageContext.request.contextPath}/monthly/edit?id=${member.id}">
                                            <button class="btn-info"
                                                    style="width: auto; padding: 6px 14px; font-size: 13px;">
                                                <i class="fas fa-edit"></i> 수정
                                            </button>
                                        </a>
                                        <!-- 삭제 버튼 -->
                                        <form action="${pageContext.request.contextPath}/monthly/delete"
                                              method="post"
                                              style="display: inline;"
                                              onsubmit="return confirm('${member.name}(${member.plateNumber}) 회원을 삭제하시겠습니까?')">
                                            <input type="hidden" name="id" value="${member.id}">
                                            <button type="submit" class="btn-danger"
                                                    style="width: auto; padding: 6px 14px; font-size: 13px;">
                                                <i class="fas fa-trash"></i> 삭제
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7">
                                <div class="no-data">
                                    <i class="fas fa-users-slash"></i>
                                    <p>등록된 월정액 회원이 없습니다.</p>
                                </div>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <!-- ========== 페이지네이션 ========== -->
        <c:if test="${totalPages > 1}">
            <div style="display: flex; justify-content: center; align-items: center; gap: 8px; margin-top: 25px;">

                <%-- 이전 버튼 --%>
                <c:choose>
                    <c:when test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/monthly/list?page=${currentPage - 1}">
                            <button class="btn-secondary" style="width: auto; padding: 8px 16px;">
                                <i class="fas fa-chevron-left"></i>
                            </button>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn-secondary" style="width: auto; padding: 8px 16px; opacity: 0.4;" disabled>
                            <i class="fas fa-chevron-left"></i>
                        </button>
                    </c:otherwise>
                </c:choose>

                <%-- 페이지 번호 --%>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <button class="btn-primary"
                                    style="width: auto; padding: 8px 14px; min-width: 40px;">${i}</button>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/monthly/list?page=${i}">
                                <button class="btn-secondary"
                                        style="width: auto; padding: 8px 14px; min-width: 40px;">${i}</button>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <%-- 다음 버튼 --%>
                <c:choose>
                    <c:when test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/monthly/list?page=${currentPage + 1}">
                            <button class="btn-secondary" style="width: auto; padding: 8px 16px;">
                                <i class="fas fa-chevron-right"></i>
                            </button>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn-secondary" style="width: auto; padding: 8px 16px; opacity: 0.4;" disabled>
                            <i class="fas fa-chevron-right"></i>
                        </button>
                    </c:otherwise>
                </c:choose>

            </div>
        </c:if>

    </div>
</div>
</body>
</html>
