<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p style="text-align: center; font-size: xx-large
    ">회원가입신청이 완료되었습니다. <br> 관리자의 인증을 기다려주세요.</p>
</body>
</html>

<script>
    //5초 뒤 로그인 창으로 이동.
    const url = '/admin/login';
    setTimeout(function (){
        window.location.href = url;
    },5000)
</script>