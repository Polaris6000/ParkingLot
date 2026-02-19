<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/change-pw.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/change-pw.js"></script>
</head>
<body>
<main>
    <form action="/admin/changepw" method="post">
        <input type="text" name="uuid" readonly style="display: none" value="<%=(String) request.getParameter("uuid")%>">
        <p>비밀번호 변경</p>
        <div>
            <label><span>비밀번호</span> <input type="password" name="pw" style="font-family: sans-serif"></label> <br>
            <label><span>비밀번호 확인</span> <input type="password" style="font-family: sans-serif"> <br></label>
        </div>
        <article>
        </article>
            <button type="submit">확인</button>
    </form>
</main>
</body>
</html>
<script>
    const params = new URLSearchParams(window.location.search);
    // result가 없거나 빈 문자열이면 ''(빈문자열)를 할당
    const result = params.get('result') || '';
    console.log(result);

    document.addEventListener("DOMContentLoaded",() => {
        if ( result === "success"){
            alert("비밀번호 변경에 성공하였습니다!")
            window.location.replace("/admin/login");
        }

    })
</script>