<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/FindInfo.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/find-info.js"></script>
</head>
<body>
<main>
    <section class="select-section">
        <div class="find_id">
            <button>아이디 찾기</button>
        </div>
        <div class="find_pw">
            <button>비밀번호 변경</button>
        </div>
    </section>


    <section class="input-section">
        <p>회원가입에 사용한 정보를 입력해주세요</p>

        <form class="find_id_group" action="/admin/findid" method="post">
            이메일 <input type="text" name="find_id_email" placeholder="example@domain.com">
        </form>

        <form class="find_pw_group" style="display: none" action="/admin/findpw" method="post">
            아이디 <input type="text" name="find_pw_id"> <br>
            이메일 <input type="text" name="find_pw_email" placeholder="example@domain.com">
        </form>

        <p>
            <button>찾기</button>
        </p>
    </section>


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
            alert("작성하신 메일로 아이디를 발송하였습니다.")
        }
        if (result === "fail"){
            alert("작성하신 메일을 찾을 수 없습니다.")
        }
        if (result === "fail_pw"){
            alert("입력하신 정보가 틀렸습니다.")
        }
    })
</script>