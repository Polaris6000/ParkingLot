<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>테스트</title>
</head>
<body>
    <h1>간단 테스트</h1>
    
    <!-- AJAX 없이 일반 폼으로 테스트 -->
    <form action="${pageContext.request.contextPath}/test/data" method="post">
        <input type="hidden" name="action" value="clearAll">
        <button type="submit">전체 초기화 (일반 폼)</button>
    </form>
    
    <hr>
    
    <!-- 입차 테스트 -->
    <form action="${pageContext.request.contextPath}/test/data" method="post">
        <input type="hidden" name="action" value="bulkEntry">
        <input type="number" name="count" value="5">
        <button type="submit">입차 5대 (일반 폼)</button>
    </form>
    
    <hr>
    <p>결과: ${message}</p>
    <p>통계: ${statistics}</p>
</body>
</html>
