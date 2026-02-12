<%@ page import="com.example.parkinglot.dto.AdminDTO" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<%
    AdminDTO admin = (AdminDTO) request.getAttribute("admin");
%>
<p style="text-align: center; font-size: xx-large
    ">회원가입이 승인되었습니다. <br> 대상자에게 알려주세요.
    <br>
    가입 신청자 : <%=admin.getName()%>
</p>

</body>
</html>

<script>

</script>