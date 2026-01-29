<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="../../static/css/admin/FindInfo.css">
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

        <div class="find_id_group">
            이메일 <input type="text" name="find_id_email" placeholder="example@domain.com">
        </div>

        <div class="find_pw_group" style="display: none">
            아이디 <input type="text" name="find_pw_id"> <br>
            이메일 <input type="text" name="find_pw_email" placeholder="example@domain.com">
        </div>

        <p>
            <button>찾기</button>
        </p>
    </section>


</main>
</body>
</html>
