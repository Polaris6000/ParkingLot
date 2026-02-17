<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>월정액 회원 수정 - 스마트주차 반월당점</title>
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
    <%-- edit도 redirect로 오기 때문에 param으로 읽음 --%>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <c:choose>
                <c:when test="${param.error == 'fail'}">수정 중 오류가 발생했습니다. 다시 시도해 주세요.</c:when>
                <c:when test="${param.error == 'empty'}">모든 항목을 입력해 주세요.</c:when>
                <c:when test="${param.error == 'invaliddate'}">날짜 형식이 올바르지 않습니다.</c:when>
                <c:when test="${param.error == 'daterange'}">만료일은 시작일보다 이후여야 합니다.</c:when>
                <c:otherwise>오류가 발생했습니다.</c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- ========== 수정 폼 카드 ========== -->
    <div class="policy-form-card">

        <!-- 카드 제목 -->
        <div class="card-title">
            <i class="fas fa-user-edit"></i> 월정액 회원 정보 수정
        </div>

        <!-- 차량번호 읽기 전용 표시 (수정 불가) -->
        <div class="policy-status-banner" style="margin-bottom: 25px;">
            <span class="status-text">
                <i class="fas fa-car"></i> 차량번호: <strong>${monthlyParkingDTO.plateNumber}</strong>
            </span>
            <span class="update-time">
                <i class="fas fa-info-circle"></i> 차량번호는 수정할 수 없습니다.
            </span>
        </div>

        <form action="${pageContext.request.contextPath}/monthly/edit" method="post">

            <!-- hidden: id, plateNumber (차량번호는 배너로 표시하고 hidden으로 전송) -->
            <input type="hidden" name="id" value="${monthlyParkingDTO.id}">
            <input type="hidden" name="plateNumber" value="${monthlyParkingDTO.plateNumber}">

            <div class="form-row">

                <!-- 이름 -->
                <div class="form-group">
                    <label for="name"><i class="fas fa-user"></i> 이름 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="name"
                           name="name"
                           value="${monthlyParkingDTO.name}"
                           maxlength="20"
                           required>
                </div>

                <!-- 연락처 -->
                <div class="form-group">
                    <label for="phoneNumber"><i class="fas fa-phone"></i> 연락처 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="phoneNumber"
                           name="phoneNumber"
                           value="${monthlyParkingDTO.phoneNumber}"
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
                           value="${monthlyParkingDTO.beginDate}"
                           required>
                </div>

                <!-- 만료일 -->
                <div class="form-group">
                    <label for="expiryDate"><i class="fas fa-calendar-xmark"></i> 만료일 <span style="color: var(--danger-color);">*</span></label>
                    <input type="date"
                           id="expiryDate"
                           name="expiryDate"
                           value="${monthlyParkingDTO.expiryDate}"
                           required>
                    <p class="help-text">만료일은 시작일보다 이후여야 합니다.</p>
                </div>

            </div>

            <!-- 버튼 그룹 -->
            <div class="btn-group">
                <button type="submit" class="btn btn-primary btn-success">
                    <i class="fas fa-save"></i> 수정 완료
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
    // 시작일 변경 시 만료일 최솟값 자동 갱신
    document.getElementById('beginDate').addEventListener('change', function () {
        const beginDate = this.value;
        const expiryDateInput = document.getElementById('expiryDate');
        if (beginDate) {
            expiryDateInput.min = beginDate;
            if (expiryDateInput.value && expiryDateInput.value < beginDate) {
                expiryDateInput.value = '';
            }
        }
    });
</script>

</body>
</html>
