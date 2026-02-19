<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <%--    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/login.css">--%>
    <%--    Bootstrap Icons → Font Awesome으로 대체하여 주석 유지                                               --%>
    <%--    <link rel="stylesheet" type="text/css"                                                               --%>
    <%--          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">       --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/login.js"></script>

    <style>

        /* ── 입력 그룹 세로 배치 ─────────────────────────────────
           public.css의 .input-group 이 flex-direction:row 이므로
           로그인 폼 내부에서만 column 으로 덮어씀                  */
        .panel-inner .input-group {
            flex-direction: column;
            gap: 12px;
            margin-bottom: 8px;
            background: transparent;
            border: none;
            padding: 0;
        }

        /* ── 로그인 상태 유지 체크박스 ───────────────────────────── */
        .login-keep-wrap {
            margin: 14px 0 4px;
        }

        .keep-check {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .input-keep {
            width: 16px;
            height: 16px;
            accent-color: var(--primary-color);
            cursor: pointer;
        }

        .keep-text {
            font-size: 13px;
            color: var(--text-gray);
            cursor: pointer;
            user-select: none;
        }

        /* ── 로그인 버튼 ─────────────────────────────────────────
           public.css .btn-primary 사용, letter-spacing 만 추가       */
        .btn-login-wrap {
            margin-top: 16px;
        }

        .btn-login {
            letter-spacing: 1px;
        }

        /* ── 회원가입 / 찾기 링크 묶음 ──────────────────────────── */
        .find-wrap {
            list-style: none;
            padding: 0;
            margin: 20px 0 0;
            display: flex;
            justify-content: center;
            border-top: 1px solid var(--border-color);
            padding-top: 16px;
        }

        /* nav > ul > li 의 position:relative 리셋 */
        .find-wrap li {
            position: static;
        }

        /* 두 번째 항목 앞 구분선 */
        .find-wrap li + li::before {
            content: "|";
            color: var(--border-color);
            margin: 0 10px;
        }

        /* public.css nav a 스타일(배경·패딩·transform) 덮어쓰기 */
        .find-text {
            font-size: 13px;
            color: var(--text-gray);
            background: transparent;
            padding: 0;
            border-radius: 0;
            font-weight: normal;
            transition: color 0.2s;
        }

        .find-text:hover {
            color: var(--primary-color);
            background: transparent;
            transform: none;
        }

    </style>
</head>
<%
    String error = (String) request.getAttribute("error");
%>
<%-- body.auth-page : public.css 섹션 20에서 세로 중앙 정렬 적용 --%>
<body class="auth-page">

<main>
    <section class="page-title">
        <div>
            <%-- .site-name-span : public.css 섹션 20 공통 스타일 --%>
            <span class="site-name-span">스마트주차 반월당점</span>
        </div>
    </section>

    <%-- .auth-container + .policy-form-card : public.css 섹션 20 공통 스타일 --%>
    <section class="auth-container">
        <div class="card policy-form-card">
            <div class="">
                <form id="frmNIDLogin" action="/admin/login" method="POST">
                    <div class="panel-inner">
                        <div class="input-group">
                            <!-- 아이디 입력 공간 -->
                            <%-- .input-item / .text-label / .auth-input : public.css 섹션 20 공통 스타일 --%>
                            <div class="input-item" id="input_item_id">
                                <label for="id" class="text-label" id="id_label"
                                >아이디</label
                                >
                                <input
                                        type="text"
                                        id="id"
                                        name="id"
                                        class="auth-input"
                                        width=" 300px"
                                />
                                <%-- .btn-delete : public.css 섹션 20 공통 스타일 --%>
                                <%-- bi bi-x-circle → fa-regular fa-circle-xmark (Font Awesome 6) --%>
                                <button
                                        tabindex="-1"
                                        type="button"
                                        class="btn-delete"
                                        id="id_clear"
                                        style="display: none"
                                >
                                    <i class="fa-regular fa-circle-xmark"></i>
                                </button>
                            </div>

                            <!-- 비밀번호 입력 공간 -->
                            <div class="input-item" id="input_item_pw">
                                <label for="pw" class="text-label" id="pw_label">비밀번호</label>
                                <input
                                        type="password"
                                        id="pw"
                                        name="pw"
                                        class="auth-input"
                                        style="font-family: sans-serif"
                                />
                                <%-- .btn-view : public.css 섹션 20 공통 스타일 --%>
                                <%-- bi bi-eye → fa-regular fa-eye (Font Awesome 6) --%>
                                <button
                                        tabindex="-1"
                                        type="button"
                                        class="btn-view hide"
                                        id="pw_hide"
                                        style="display: none"
                                >
                                    <i class="fa-regular fa-eye" id="icon_view"></i>
                                </button>
                                <button
                                        tabindex="-1"
                                        type="button"
                                        class="btn-delete"
                                        id="pw_clear"
                                        style="display: none"
                                >
                                    <i class="fa-regular fa-circle-xmark"></i>
                                </button>
                            </div>
                        </div>

                        <!-- 로그인 상태 기억할것인지 체크박스 -->
                        <div class="login-keep-wrap" id="login_keep_wrap">
                            <div
                                    class="keep-check"
                                    id="keep"
                                    role="checkbox"
                                    aria-checked="false"
                            >
                                <input
                                        type="checkbox"
                                        id="remember-me"
                                        name="remember-me"
                                        class="input-keep"
                                        value="true"
                                />
                                <label for="remember-me" class="keep-text"
                                >로그인 상태 유지</label
                                >
                            </div>
                        </div>

                        <!-- 오류 메세지 표기 구간 -->
                        <%-- .auth-error-wrap / .alert.alert-error : public.css 섹션 20 공통 스타일 --%>
                        <div class="auth-error-wrap">
                            <div id="err_empty_id" style="display: none">
                                <div class="alert alert-error">
                                    <strong>아이디</strong>를 입력해 주세요.
                                </div>
                            </div>

                            <div id="err_empty_pw" style="display: none">
                                <div class="alert alert-error">
                                    <strong>비밀번호</strong>를 입력해 주세요.
                                </div>
                            </div>

                            <div id="err_wrong_data">
                                <%-- error == null → <strong> 없음 → CSS :not(:has(strong)) 으로 자동 숨김 --%>
                                <div class="alert alert-error">
                                    <%
                                        if (error != null) {
                                    %>
                                    <strong>아이디</strong> 혹은 <strong>비밀번호</strong>를
                                    잘못 입력하였습니다.
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>

                        <!-- 로그인 버튼 -->
                        <%-- .btn-primary(public.css) + .btn-login(letter-spacing 추가용) --%>
                        <div class="btn-login-wrap">
                            <button
                                    type="submit"
                                    class="btn-primary btn-login off next_step nlog-click"
                                    id="log.login"
                            >
                                <span class="btn-text" id="log.login.text">로그인</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- 각종 찾기, 회원가입 묶음 -->
            <ul class="find-wrap" id="find_wrap">
                <li>
                    <a target="_blank"
                       href="/admin/signup"
                       id="join"
                       class="find-text">
                        회원가입</a>
                </li>
                <li>
                    <a target="_blank"
                       href="/admin/findinfo"
                       id="pwinquiry"
                       class="find-text">
                        아이디/비밀번호 찾기
                    </a>
                </li>
            </ul>

            <!--                <ul>-->
            <!--                    <li th:text="${findUser.id}">아이디</li>-->
            <!--                    <li th:text="${findUser.pw}">비번</li>-->
            <!--                    <li th:text="${findUser.nickname}">닉네임</li>-->
            <!--                </ul>-->
        </div>
    </section>
</main>
</body>
</html>
