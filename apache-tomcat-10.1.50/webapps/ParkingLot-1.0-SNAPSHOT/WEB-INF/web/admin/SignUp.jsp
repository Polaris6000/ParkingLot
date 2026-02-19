<%@ page import="java.util.List" %>
<%@ page import="com.example.parkinglot.dto.AdminDTO" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title>스마트주차 반월당점</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/admin/SignUp.css">
    <script defer src="${pageContext.request.contextPath}/static/js/admin/SignUp.js"></script>
</head>
<body>
<%
    List<AdminDTO> adminDTOList = (List<AdminDTO>) request.getAttribute("masters");
%>

<main>
    <section>
        <section class="site-name">
            <div>
                <span class="site-name-link">스마트 반월당 주차장 관리자 회원가입</span>
            </div>
        </section>
        <div class="data-input-div">
            <form action="/admin/signup" method="post">
                <div class="user-text-div">
                    <div>
                        <p><span class="star">*</span>표는 필수 기입 대상입니다.</p>
                    </div>

                    <div class="two-data-text" id="email-div">
                        <span>이메일 주소<span class="star">*</span></span>
                        <input type="text" name="email" placeholder="exemple@domain.com">
                        <p style="display: none" class="red">메일 주소의 형식이 올바르지 않습니다.</p>
                        <p style="display: none" class="red">이미 등록된 메일주소 입니다.</p>
                        <p style="display: none" class="green">사용 가능한 메일입니다.</p>
                    </div>


                    <div class="three-data-text" id="id-div">
                        <span>아이디<span class="star">*</span></span> <input type="text" name="id">
                        <p>영어 혹은 숫자를 포함하는 4~20자로 구성해주세요.</p>
                        <p style="display: none;" class="red">이미 사용중인 아이디 입니다.</p>
                        <p style="display: none;" class="green">사용 가능한 아이디 입니다.</p>
                        <p style="display: none;" class="red">사용 불가능한 아이디 입니다.</p>

                    </div>

                    <div class="three-data-text" id="name-div">
                        <span>이름<span class="star">*</span></span> <input type="text" name="name">
                        <p>이름은 한글 6자, 영어 18자까지 사용 가능합니다.</p>
                        <p style="display: none;" class="green">사용가능합니다.</p>
                        <p style="display: none;" class="red">길이 제한을 초과했습니다.</p>
                    </div>

                    <div class="four-data-text" id="pw-div">
                        <span>비밀번호<span class="star">*</span></span> <input type="password" name="pw"> <br>
                        <span>비밀번호 확인<span class="star">*</span></span> <input type="password"> <br>
                        <p>영어 숫자 특문 섞어서 8글자이상 16글자 이하로 구성해주세요</p>
                        <p style="display: none" class="green">사용 가능한 비밀번호입니다.</p>
                        <p style="display: none" class="red">사용 불가능한 비밀번호입니다.</p>
                        <p style="display: none" class="red">비밀번호가 일치하지 않습니다!</p>
                    </div>

                    <div class="two-data-text" id="auth-div">
                        <span>인증대상</span>
                        <select name="master" id="master">
<%--                                반복문으로 마스터의 정보를 가져오기.--%>
                            <%
                                for (AdminDTO adminDTO : adminDTOList) {
                            %>
                            <option value="<%=adminDTO.getEmail()%>"><%=adminDTO.getName()%></option>
                            <%
                                }
                            %>
                        </select>
<%--                        <input type="select" name="addr">--%>
                        <p>인증 받을 대상을 선택해주세요.</p>
                    </div>
                </div>


                <button class="signup-btn" type="submit">
                    회원가입
                </button>
            </form>
        </div>
    </section>
</main>
</body>
</html>
