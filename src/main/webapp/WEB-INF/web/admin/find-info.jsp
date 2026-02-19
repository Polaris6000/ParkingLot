<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <%--    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/find-info.css">--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/find-info.js"></script>

    <%-- =====================================================
         find-info.jsp 전용 스타일
         공통 스타일은 public.css 섹션 20 (인증 페이지 공통) 참고
         - body.auth-page        : 세로 중앙 정렬
         - .auth-container       : 카드 래퍼
         - .input-item           : 입력 묶음
         - .text-label           : 라벨
         - .auth-input           : 입력 필드
         - .tab-buttons/.tab-btn : 탭 전환 UI (public.css 섹션 15)
         ===================================================== --%>
    <style>

        /* ── 탭 버튼 영역 ─────────────────────────────────────
           public.css의 .tab-buttons/.tab-btn 을 그대로 사용
           카드 상단에 딱 붙도록 margin-bottom 만 조정           */
        .select-section {
            width: 460px;               /* .auth-container .policy-form-card 와 너비 맞춤 */
        }

        /* ── 입력 섹션 (폼 영역) ─────────────────────────────── */
        .input-section {
            width: 100%;
        }

        /* 안내 문구 */
        .input-section > p {
            font-size: 14px;
            color: var(--text-gray);
            margin: 0 0 20px;
            text-align: center;
        }

        /* ── 아이디 / 비밀번호 폼 그룹 ───────────────────────── */
        .find-id-group,
        .find-pw-group {
            display: flex;
            flex-direction: column;
            gap: 14px;
            margin-bottom: 0;
        }

        /* ── 찾기 버튼 영역 ──────────────────────────────────── */
        .find-btn-wrap {
            margin-top: 20px;
        }

    </style>
</head>
<%-- body.auth-page : public.css 섹션 20에서 세로 중앙 정렬 적용 --%>
<body class="auth-page">
<main>

    <%-- .tab-buttons / .tab-btn : public.css 섹션 15 탭 UI 그대로 사용 --%>
    <section class="tab-buttons select-section">
        <div class="find-id">
            <button class="tab-btn active">아이디 찾기</button>
        </div>
        <div class="find-pw">
            <button class="tab-btn">비밀번호 변경</button>
        </div>
    </section>

    <%-- .auth-container + .policy-form-card : public.css 섹션 20 공통 스타일 --%>
    <section class="auth-container input-section">
        <div class="card policy-form-card">

            <%-- .card-title : public.css 섹션 19 카드 제목 스타일 --%>
            <p class="card-title">회원가입에 사용한 정보를 입력해주세요</p>

            <%-- 아이디 찾기 폼 (이메일 입력) --%>
            <form class="find-id-group" action="/admin/findid" method="post">
                <%-- .input-item / .text-label / .auth-input : public.css 섹션 20 공통 스타일 --%>
                <div class="input-item">
                    <label class="text-label" for="find_id_email">이메일</label>
                    <input
                            type="text"
                            id="find_id_email"
                            name="find_id_email"
                            class="auth-input"
                            placeholder="example@domain.com"
                    >
                </div>
                <%-- .btn-primary : public.css 섹션 13 버튼 스타일 --%>
                <div class="find-btn-wrap">
                    <button class="btn-primary" type="submit">찾기</button>
                </div>
            </form>

            <%-- 비밀번호 변경 폼 (아이디 + 이메일 입력) --%>
            <form class="find-pw-group" style="display: none" action="/admin/findpw" method="post">
                <div class="input-item">
                    <label class="text-label" for="find_pw_id">아이디</label>
                    <input
                            type="text"
                            id="find_pw_id"
                            name="find_pw_id"
                            class="auth-input"
                    >
                </div>
                <div class="input-item">
                    <label class="text-label" for="find_pw_email">이메일</label>
                    <input
                            type="text"
                            id="find_pw_email"
                            name="find_pw_email"
                            class="auth-input"
                            placeholder="example@domain.com"
                    >
                </div>
                <div class="find-btn-wrap">
                    <button class="btn-primary" type="submit">찾기</button>
                </div>
            </form>

        </div>
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
