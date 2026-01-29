<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/Login.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/Login.js"></script>
</head>
<body>
<main>
    <section class="site-name">
        <div>
            <span class="site-name-span">주차장 관리 시스템</span>
        </div>
    </section>

    <section class="input-space">
        <!-- content -->
        <div class="content">
            <div class="login_wrap">
                <form id="frmNIDLogin" th:action="@{/user/login}" method="POST">
                    <div class="panel_inner">
                        <div class="login_box">
                            <!--                                        아이디 입력 공간-->
                            <div class="input_item id" id="input_item_id">
                                <label for="id" class="text_label" id="id_label"
                                >아이디</label
                                >
                                <input
                                        type="text"
                                        id="id"
                                        name="id"
                                        class="input_id"
                                />

                                <button
                                        type="button"
                                        class="btn_delete"
                                        id="id_clear"
                                        style="display: none"
                                >
                                    <i class="bi bi-x-circle"></i>
                                </button>
                            </div>

                            <!--                                        비밀번호 입력 공간-->
                            <div class="input_item pw" id="input_item_pw">
                                <label for="pw" class="text_label" id="pw_label"
                                >비밀번호</label
                                >
                                <input
                                        type="password"
                                        id="pw"
                                        name="pw"
                                        class="input_pw"
                                />

                                <button
                                        type="button"
                                        class="btn_view hide"
                                        id="pw_hide"
                                        style="display: none"
                                >
                                    <i class="bi bi-eye" id="icon_view"></i>
                                </button>

                                <button
                                        type="button"
                                        class="btn_delete"
                                        id="pw_clear"
                                        style="display: none"
                                >
                                    <i class="bi bi-x-circle"></i>
                                </button>
                            </div>
                        </div>

                        <!--로그인 상태 기억할것인지 체크박스-->
                        <div class="login_keep_wrap" id="login_keep_wrap">
                            <div
                                    class="keep_check"
                                    id="keep"
                                    role="checkbox"
                                    aria-checked="false"
                                    tabindex="0"
                            >
                                <input
                                        type="checkbox"
                                        id="remember-me"
                                        name="remember-me"
                                        tabindex="-1"
                                        class="input_keep"
                                        value="true"
                                />
                                <label for="remember-me" class="keep_text"
                                >로그인 상태 유지</label
                                >
                            </div>
                        </div>

                        <!--                                오류 메세지 표기 구간-->
                        <div class="login_error_wrap">
                            <div id="err_empty_id" style="display: none">
                                <div class="error_message">
                                    <strong>아이디</strong>를 입력해 주세요.
                                </div>
                            </div>

                            <div id="err_empty_pw" style="display: none">
                                <div class="error_message">
                                    <strong>비밀번호</strong>를 입력해 주세요.
                                </div>
                            </div>

                            <div id="err_wrong_data" style="display: none">
                                <div class="error_message">
                                    <strong>아이디</strong> 혹은 <strong>비밀번호</strong>를
                                    잘못 입력하였습니다.
                                </div>
                            </div>
                        </div>

                        <!--                                로그인 버튼-->
                        <div class="btn_login_wrap">
                            <button
                                    type="submit"
                                    class="btn_login off next_step nlog-click"
                                    id="log.login"
                            >
                                <span class="btn_text" id="log.login.text">로그인</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!--            각종 찾기, 회원가입 묶음.-->
            <ul class="find_wrap" id="find_wrap">
                <li>
                    <a
                            target="_blank"
                            href="/web/admin/Signup.jsp"
                            id="join"
                            class="find_text"
                    >회원가입</a
                    >
                </li>
                <li>
                    <a
                            target="_blank"
                            href="/web/admin/FindInfo.jsp"
                            id="pwinquiry"
                            class="find_text"
                    >아이디/비밀번호 찾기</a
                    >
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
