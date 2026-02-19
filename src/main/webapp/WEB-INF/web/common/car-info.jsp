<%@ page import="java.time.LocalDateTime" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주차 상세 정보</title>
</head>
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">

    <!-- Font Awesome (아이콘) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<body>
<form class="info-box" method="post">
<%--    숨김영역--%>
    <div style="display: none;" id="hiddenData">
        <input type="text" id="id" value="" name="id" readonly>
        <input type="text" id="enterTimeBackUp" value="" name="enterTimeBackUp" readonly>
        <input type="datetime-local" id="payTime" value="" name="payTime" readonly>
    </div>

    <div class="info-row">
        <label for="today" class="info-label">날짜</label>
        <input type="text" class="info-value value-green" id="today" readonly/>
    </div>

    <div class="info-row">
        <label for="carNumber" class="info-label">차량 번호</label>
        <input type="text" class="info-value value-green" id="carNumber" name="carNumber"/>
    </div>

    <div class="info-row">
        <label for="status" class="info-label">상태</label>
        <input type="text" class="info-value value-green" id="status" name="stat" readonly/>
    </div>

    <div class="info-row">
        <label class="info-label">할인 정보</label>
        <select class="info-value value-green" id="discount" name="discount">
            <option value="normal">
                일반
            </option>
            <option value="disabled">
                장애인
            </option>
            <option value="light">
                경차
            </option>
            <option value="monthly">
                월간 회원(무료 적용)
            </option>
        </select>
    </div>

    <div class="info-row">
        <label class="info-label">입차 시간</label>
        <input type="text" class="info-value value-green" id="enterTime" value="" readonly/>
    </div>

    <div class="info-row">
        <label class="info-label">출차 시간</label>
        <input type="text" class="info-value value-green" id="exitTime" readonly/>
    </div>

    <div class="info-row">
        <label class="info-label">비용</label>
        <input type="text" class="info-value value-green" id="cost" name="cost" readonly/>
    </div>
</form>

</body>
</html>
