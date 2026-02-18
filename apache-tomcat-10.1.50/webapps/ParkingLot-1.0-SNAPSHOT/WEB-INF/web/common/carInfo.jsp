<%@ page import="java.time.LocalDateTime" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주차 상세 정보</title>
</head>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
        background-color: #FAFAFA;
        padding: 40px 20px;
    }

    .info-box {
        background: #FFFFFF;
        border: 1px solid #E8E8E8;
        border-radius: 12px;
        width: 100%;
        height: 100%;
        margin: 0 auto;
        overflow: hidden;
        display:flex;
        flex-direction: column;
    }

    .info-row {
        display: flex;
        border-bottom: 1px solid #F0F0F0;
        height: calc(100% / 7);
    }

    .info-row:last-child {
        border-bottom: none;
    }

    .info-label {
        display: block;
        width: 200px;
        align-items: center;
        padding: 0 20px;
        font-size: 20px;
        font-weight: 500;
        color: #1F1F1F;
        background-color: #FAFAFA;
        align-content: center;
    }

    .info-value {
        display: block;
        align-items: center;
        width: 300px;
        border: none;
        padding: 0 20px;
        font-size: 20px;
        font-weight: 600;
        color: #1F1F1F;
        background-color: #FFFFFF;
        text-align: center;
    }
    select{
        width: 340px;
    }
    .info-value:focus{
        outline: none;
    }

    .value-green {
        color: #4CAF50;
    }



    .subtitle {
        font-size: 12px;
        font-weight: 400;
        color: #9E9E9E;
    }
</style>
<body>
<form class="info-box" method="post">
<%--    숨김영역--%>
    <div style="display: none;" id="hiddenData">
        <input type="text" id="id" value="" name="id" readonly>
        <input type="text" id="enterTimeBackUp" value="" name="enterTimeBackUp" readonly>
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
