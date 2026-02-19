package com.example.parkinglot.enums;

public enum AuthKind {
    SIGNUP,
    FINDID,
    FINDPW,
    UPGRADE;

    private final String DOMAIN_PATH = "http://localhost:8081";
    private final String AUTHENTICATION_PATH = DOMAIN_PATH + "/authentication/signup";
    private final String AUTHORIZATION_PATH = DOMAIN_PATH + "/authentication/authorization";
    //    private final String findId = domainPath+ "/authentication/findid";
    private final String CHANGE_PW = DOMAIN_PATH + "/admin/changepw";

    //종류를 반환
    public String getAuthKind(){
        return switch (this) {
            case SIGNUP -> "signUp";
            case FINDID -> "findPw";
            case UPGRADE -> "upgrade";
            case FINDPW -> "findPw";
        };
    }

    //메일 제목 반환
    public String getSubject() {
        return switch (this) {
            case SIGNUP -> "[Parking Lot]관리자 회원가입 요청 메일입니다.";
            case FINDID -> "[Parking Lot]아이디 찾기 확인 메일입니다.";
            case FINDPW -> "[Parking Lot]비밀번호 찾기 확인 메일입니다.";
            case UPGRADE -> "[Parking Lot]관리자 등급 상향 요청 메일입니다.";
        };

    }

    //권한과 상관없이 이용할 수 있는 기능은 이곳으로.
    public String getFindContext(String word) {

        String findIdWord = "찾으시는 ID는 "+ word + "입니다.";
        String findPwWord = "새로운 비밀번호로 변경하려면 아래 url클릭해주세요. <br>" +
                "<a href= \"" + CHANGE_PW + "?uuid=" + word + "\"> 비밀번호 바꾸기 </a>";

        return switch (this) {
            case SIGNUP -> null;
            case FINDID -> findIdWord; //id 바로 표시
            case FINDPW -> findPwWord; //uuid 보내기
            case UPGRADE -> null;
        };
    }

    //권한과 관련된 내용은 이곳으로.
    public String getAuthContext(String word, String uuid) {
        //to String해서 유저 정보, uuid로 링크
        String upgradeWord = "신청 유저 입니다. : " + word +
                "<br> 등급을 master로 올리려면 아래 url클릭해주세요. <br>" +
                "<a href= \"" + AUTHORIZATION_PATH + "?uuid=" + uuid + "\"> 등급 올리기 </a>";

        //to String해서 유저 정보, uuid로 링크
        String signUpWord = "신청 유저 입니다. : " + word +
                "<br>  가입을 수락하시려면 아래 url클릭해주세요. <br>" +
                "<a href= \"" + AUTHENTICATION_PATH + "?uuid=" + uuid + "\"> 가입 수락 </a>";

        return switch (this) {
            case SIGNUP -> signUpWord;
            case FINDID -> null;
            case FINDPW -> null;
            case UPGRADE -> upgradeWord;
        };
    }


}
