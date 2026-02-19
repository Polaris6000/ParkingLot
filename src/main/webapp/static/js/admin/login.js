//id입력칸 관련 변수
// 수정: id 기반으로 변경 (다른 셀렉터들과 일관성 유지)
const idInput = document.querySelector("input#id");
const idLabel = document.querySelector("label[id='id_label']");
const idDelBtn = document.querySelector("button[id='id_clear']");
//pw입력칸 관련 변수
// 수정: id 기반으로 변경 (다른 셀렉터들과 일관성 유지)
const idInput = document.querySelector("input#pw");
const pwLabel = document.querySelector("label[id='pw_label']");
const pwDelBtn = document.querySelector("button[id='pw_clear']");
const pwViewBtn = document.querySelector("button[id='pw_hide']")
const eyeView = pwViewBtn.querySelector("i");
//login버튼과 그 외 오류 메세지들
const loginBtn = document.querySelector("button[id='log.login']");
const idError = document.querySelector("div [id='err_empty_id']");
const pwError = document.querySelector("div [id='err_empty_pw']");
const dataError = document.querySelector("div [id='err_wrong_data']");


//키다운이 시작되면 아이디가 사라져야함.
idInput.onkeydown = () => {
    if (idInput.value !== "") {
        idLabel.style.display = "none"
        idDelBtn.style.display = "block";
    }
}

//아이디 칸에서 값이 사라져야 원상태로 복구시킴.
idInput.onchange = () => {
    if (idInput.value === "") {
        idDelBtn.style.display = "none";
        idLabel.style.display = "block";
    }
}

//아이디칸 지우는거 하면 id값지우고 아이디 다시 띄우기
idDelBtn.onclick = () => {
    idInput.value = "";
    idDelBtn.style.display = "none";
    idLabel.style.display = "block";
    //커서를 저기로 옮겨줘야함.
    idInput.focus();
}

//위 과정을 동일하게 해줘야함. + view 기능까지
//키다운이 시작되면 비밀번호 가 사라져야함.
pwInput.onkeydown = () => {
    if (pwInput.value !== "") {
        pwLabel.style.display = "none"
        pwDelBtn.style.display = "block";
        pwViewBtn.style.display = "block";
    }
}

//비밀번호 칸에서 값이 사라져야 원상태로 복구시킴.
pwInput.onchange = () => {
    if (pwInput.value === "") {
        pwDelBtn.style.display = "none";
        pwViewBtn.style.display = "none";
        pwLabel.style.display = "block";
    }
}

//비밀번호 칸 지우는거 하면 pw값지우고 비번 다시 띄우기
pwDelBtn.onclick = () => {
    pwInput.value = "";
    pwDelBtn.style.display = "none";
    pwViewBtn.style.display = "none";
    pwLabel.style.display = "block";
    pwInput.type = "password";
    //커서를 저기로 옮겨줘야함.
    pwInput.focus();
}
//비번을 보고 싶다구요? 바로 보여줘. 숨기고 싶어? 바로 숨겨
pwViewBtn.onclick = () => {

    // 수정: FA 아이콘 기준으로 변경
    if (eyeView.classList.contains("fa-eye")) {
        eyeView.classList.replace("fa-eye", "fa-eye-slash");
        pwInput.type = "text";
        return;
    }
    if (eyeView.classList.contains("fa-eye-slash")) {
        eyeView.classList.replace("fa-eye-slash", "fa-eye");
        pwInput.type = "password";
        return;
    }
}


//로그인 버튼 눌렀을 때
/*
1. 아이디가 없을 때
2. 아이디가 ㅇ있는데 비번이 없을 때
3. 둘다 입력하고 로그인 했을 때
 - 서버에서 비교
 - 로그인 여부 판별
 - 성공 : main으로 돌려 보내고
 - 실패 : 로그인에 실패했습니다! 하고나서 다시 로그인 화면으로
* */
loginBtn.onclick = event => {
    idError.style.display = "none";
    pwError.style.display = "none";
    dataError.style.display = "none";


    if (idInput.value === '') {
        idError.style.display = "block";
        event.preventDefault();
        return
    }
    if (pwInput.value === '') {
        pwError.style.display = "block";
        event.preventDefault();
        return
    }


    //둘 다 입력 되있으면 로그인 처리해주기.
}