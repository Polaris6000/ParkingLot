<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>월정액 회원 등록 - 스마트주차 반월당점</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">

    <!-- ========== 헤더 ========== -->
    <header class="dashboard-header">
        <%@include file="../common/header.jsp" %>
    </header>

    <!-- ========== 오류 메시지 ========== -->
    <%-- register는 redirect로 오기 때문에 param으로 읽음 --%>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <c:choose>
                <c:when test="${param.error == 'duplicate'}">이미 등록된 차량번호입니다.</c:when>
                <c:when test="${param.error == 'empty'}">모든 항목을 입력해 주세요.</c:when>
                <c:when test="${param.error == 'invaliddate'}">날짜 형식이 올바르지 않습니다.</c:when>
                <c:when test="${param.error == 'daterange'}">만료일은 시작일보다 이후여야 합니다.</c:when>
                <c:otherwise>입력 오류가 발생했습니다. 다시 시도해 주세요.</c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- ========== 등록 폼 카드 ========== -->
    <div class="policy-form-card">

        <!-- 카드 제목 -->
        <div class="card-title">
            <i class="fas fa-user-plus"></i> 월정액 회원 신규 등록
        </div>

        <form action="${pageContext.request.contextPath}/monthly/register" method="post">

            <div class="form-row">

                <!-- 차량번호 -->
                <div class="form-group">
                    <label for="plateNumber"><i class="fas fa-car"></i> 차량번호 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="plateNumber"
                           name="plateNumber"
                           placeholder="예) 123가4567"
                           maxlength="20"
                           required>
                    <p class="help-text">차량번호는 중복 등록이 불가합니다.</p>
                </div>

                <!-- 이름 -->
                <div class="form-group">
                    <label for="name"><i class="fas fa-user"></i> 이름 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="name"
                           name="name"
                           placeholder="회원 이름"
                           maxlength="20"
                           required>
                </div>

            </div>

            <div class="form-row">

                <!-- 연락처 -->
                <div class="form-group">
                    <label for="phoneNumber"><i class="fas fa-phone"></i> 연락처 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="phoneNumber"
                           name="phoneNumber"
                           placeholder="예) 010-1234-5678"
                           maxlength="13"
                           required>
                    <p class="help-text">형식: 010-0000-0000 (최대 13자)</p>
                </div>

            </div>

            <div class="form-row">

                <!-- 시작일 -->
                <div class="form-group">
                    <label for="beginDate"><i class="fas fa-calendar-plus"></i> 시작일 <span style="color: var(--danger-color);">*</span></label>
                    <input type="date"
                           id="beginDate"
                           name="beginDate"
                           required>
                </div>

                <!-- 만료일 -->
                <div class="form-group">
                    <label for="expiryDate"><i class="fas fa-calendar-xmark"></i> 만료일 <span style="color: var(--danger-color);">*</span></label>
                    <input type="date"
                           id="expiryDate"
                           name="expiryDate"
                           required>
                    <p class="help-text">만료일은 시작일보다 이후여야 합니다.</p>
                </div>

            </div>

            <!-- 버튼 그룹 -->
            <div class="btn-group">
                <button type="submit" class="btn btn-primary btn-success">
                    <i class="fas fa-save"></i> 등록
                </button>
                <a href="${pageContext.request.contextPath}/monthly/list" style="flex: 1;">
                    <button type="button" class="btn btn-secondary" style="width: 100%;">
                        <i class="fas fa-arrow-left"></i> 목록으로
                    </button>
                </a>
            </div>

        </form>
    </div>

</div>

<script>
    // 시작일 선택 시 만료일 최솟값 자동 설정
    document.getElementById('beginDate').addEventListener('change', function () {
        const beginDate = this.value;
        const expiryDateInput = document.getElementById('expiryDate');
        if (beginDate) {
            expiryDateInput.min = beginDate;
            // 만료일이 시작일보다 이전이면 초기화
            if (expiryDateInput.value && expiryDateInput.value < beginDate) {
                expiryDateInput.value = '';
            }
        }
    });

    // 페이지 로드 시 오늘 날짜를 시작일 기본값으로
    window.addEventListener('DOMContentLoaded', function () {
        const today = new Date().toISOString().split('T')[0];
        const beginDateInput = document.getElementById('beginDate');
        if (!beginDateInput.value) {
            beginDateInput.value = today;
            document.getElementById('expiryDate').min = today;
        }
    });
</script>

</body>
</html>
