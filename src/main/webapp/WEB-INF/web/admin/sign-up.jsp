<%@ page import="java.util.List" %>
<%@ page import="com.example.parkinglot.dto.AdminDTO" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <%--    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/sign-up.css">--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/sign-up.js"></script>

    <%-- =====================================================
         sign-up.jsp 전용 스타일
         공통 스타일은 public.css 섹션 20 (인증 페이지 공통) 참고
         - body.auth-page        : 세로 중앙 정렬
         - .auth-container       : 카드 래퍼
         - .input-item           : 입력 묶음
         - .text-label           : 라벨
         - .auth-input           : 입력 필드
         ===================================================== --%>
    <style>

        /* ── 카드 너비 확장 ──────────────────────────────────────
           SignUp은 필드가 많아 Login/FindInfo보다 넓게 설정
           .auth-container .policy-form-card 는 460px 이 기본이므로 오버라이드 */
        .auth-container .policy-form-card {
            width: 560px;
        }

        /* ── 폼 전체 묶음 ────────────────────────────────────── */
        .signup-form-wrap {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        /* ── 개별 필드 묶음 ──────────────────────────────────────
           .input-item(public.css 섹션 20)에 하단 힌트/피드백 문구 공간 추가 */
        .form-item {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        /* ── 필수 입력 별표 ──────────────────────────────────── */
        .star {
            color: var(--danger-color);     /* --danger-color: #e74a3b */
            font-size: 14px;
            margin-left: 2px;
        }

        /* ── 힌트/피드백 문구 공통 ───────────────────────────────
           기본(회색 안내문), 성공(초록), 오류(빨강)               */
        .field-hint {
            font-size: 12px;
            color: var(--text-gray);
            margin: 0;
        }

        .field-success {
            font-size: 12px;
            color: var(--ev-green);         /* --ev-green: #52c41a */
            margin: 0;
            animation: slideDown 0.3s ease; /* public.css 키프레임 재사용 */
        }

        .field-error {
            font-size: 12px;
            color: var(--danger-color);     /* --danger-color: #e74a3b */
            margin: 0;
            animation: slideDown 0.3s ease; /* public.css 키프레임 재사용 */
        }

        /* ── select 드롭다운 ─────────────────────────────────────
           .auth-input 과 동일 톤으로 맞춤                          */
        .auth-select {
            width: 100%;
            box-sizing: border-box;
            padding: 11px 14px;
            border: 1.5px solid var(--border-color);
            border-radius: 8px;
            font-size: 15px;
            font-family: var(--main-font), sans-serif;
            color: var(--text-dark);
            background: var(--white);
            transition: border-color 0.2s, box-shadow 0.2s;
            outline: none;
            cursor: pointer;
        }

        .auth-select:focus {
            border-color: var(--accent-blue);
            box-shadow: 0 0 0 3px rgba(92, 124, 250, 0.12);
        }

        /* ── 회원가입 버튼 영역 ──────────────────────────────────
           .btn-primary(public.css) 사용, 상단 여백만 추가          */
        .signup-btn-wrap {
            margin-top: 8px;
        }

    </style>
</head>
<%
    List<AdminDTO> adminDTOList = (List<AdminDTO>) request.getAttribute("masters");
%>
<%-- body.auth-page : public.css 섹션 20에서 세로 중앙 정렬 적용 --%>
<body class="auth-page">

<main>
    <section class="page-title">
        <%-- .site-name-span : public.css 섹션 20 공통 스타일 --%>
        <span class="site-name-span">스마트 반월당 주차장 관리자 회원가입</span>
    </section>

    <%-- .auth-container + .policy-form-card : public.css 섹션 20 공통 스타일 --%>
    <section class="auth-container">
        <div class="card policy-form-card">

            <form action="/admin/signup" method="post" class="signup-form-wrap">

                <%-- 필수 입력 안내 --%>
                <p class="field-hint"><span class="star">*</span>표는 필수 기입 대상입니다.</p>

                <%-- 이메일 --%>
                <%-- .form-item / .text-label / .auth-input : 공통 스타일 활용 --%>
                <div class="form-item" id="email-div">
                    <label class="text-label" for="email">
                        이메일 주소<span class="star">*</span>
                    </label>
                    <input type="text" id="email" name="email" class="auth-input" placeholder="example@domain.com">
                    <p style="display: none" class="field-error">메일 주소의 형식이 올바르지 않습니다.</p>
                    <p style="display: none" class="field-error">이미 등록된 메일주소 입니다.</p>
                    <p style="display: none" class="field-success">사용 가능한 메일입니다.</p>
                </div>

                <%-- 아이디 --%>
                <div class="form-item" id="id-div">
                    <label class="text-label" for="id">
                        아이디<span class="star">*</span>
                    </label>
                    <input type="text" id="id" name="id" class="auth-input">
                    <p class="field-hint">영어 혹은 숫자를 포함하는 4~20자로 구성해주세요.</p>
                    <p style="display: none;" class="field-error">이미 사용중인 아이디 입니다.</p>
                    <p style="display: none;" class="field-success">사용 가능한 아이디 입니다.</p>
                    <p style="display: none;" class="field-error">사용 불가능한 아이디 입니다.</p>
                </div>

                <%-- 이름 --%>
                <div class="form-item" id="name-div">
                    <label class="text-label" for="name">
                        이름<span class="star">*</span>
                    </label>
                    <input type="text" id="name" name="name" class="auth-input">
                    <p class="field-hint">이름은 한글 6자, 영어 18자까지 사용 가능합니다.</p>
                    <p style="display: none;" class="field-success">사용가능합니다.</p>
                    <p style="display: none;" class="field-error">길이 제한을 초과했습니다.</p>
                </div>

                <%-- 비밀번호 --%>
                <div class="form-item" id="pw-div">
                    <label class="text-label" for="pw">
                        비밀번호<span class="star">*</span>
                    </label>
                    <input type="password" id="pw" name="pw" class="auth-input" style="font-family: sans-serif">
                    <label class="text-label" for="pw-confirm">
                        비밀번호 확인<span class="star">*</span>
                    </label>
                    <input type="password" id="pw-confirm" class="auth-input" style="font-family: sans-serif">
                    <p class="field-hint">영어 숫자 특문 섞어서 8글자이상 16글자 이하로 구성해주세요</p>
                    <p style="display: none" class="field-success">사용 가능한 비밀번호입니다.</p>
                    <p style="display: none" class="field-error">사용 불가능한 비밀번호입니다.</p>
                    <p style="display: none" class="field-error">비밀번호가 일치하지 않습니다!</p>
                </div>

                <%-- 인증 대상 선택 --%>
                <%-- .auth-select : .auth-input 과 동일 톤의 select 전용 스타일 --%>
                <div class="form-item" id="auth-div">
                    <label class="text-label" for="master">인증대상</label>
                    <select name="master" id="master" class="auth-select">
                        <%-- 반복문으로 마스터의 정보를 가져오기. --%>
                        <%
                            for (AdminDTO adminDTO : adminDTOList) {
                        %>
                        <option value="<%=adminDTO.getEmail()%>"><%=adminDTO.getName()%></option>
                        <%
                            }
                        %>
                    </select>
                    <%--  <input type="select" name="addr"> --%>
                    <p class="field-hint">인증 받을 대상을 선택해주세요.</p>
                </div>

                <%-- .btn-primary : public.css 섹션 13 버튼 스타일 --%>
                <div class="signup-btn-wrap">
                    <button class="btn-primary signup-btn" type="submit">회원가입</button>
                </div>

            </form>
        </div>
    </section>
</main>
</body>
</html>
