//폼값 추가
const dataForm = document.querySelector('form');
//이메일 주소 대상
const emailDiv = document.querySelector("div[id='email-div']");
const emailInput = emailDiv.querySelector('input');
const emailWrongP = emailDiv.querySelectorAll('p')[0];
const emailDuplicateP = emailDiv.querySelectorAll('p')[1];
const emailSuccessP = emailDiv.querySelectorAll('p')[2];
let emailPass = false;
//아이디 관련 대상
const idDiv = document.querySelector("div[id='id-div']");
const idInput = idDiv.querySelector("input");
const idDuplicateP = idDiv.querySelectorAll("p")[1];
const idSuccessP = idDiv.querySelectorAll("p")[2];
const idFailP = idDiv.querySelectorAll("p")[3];
let idPass = false;
//닉네임 관련 대상
const nameDiv = document.querySelector("div[id='name-div']");
const nameInput = nameDiv.querySelector("input");
const nameSuccessP = nameDiv.querySelectorAll('p')[1];
const nameFailP = nameDiv.querySelectorAll('p')[2];
let namePass = false;
//비밀번호 관련 대상
const pwDiv = document.querySelector("div[id='pw-div']");
const pwInput = pwDiv.querySelectorAll('input')[0];
const pwRepeatInput = pwDiv.querySelectorAll('input')[1];
const pwSuccessP = pwDiv.querySelectorAll('p')[1];
const pwFailP = pwDiv.querySelectorAll('p')[2];
const pwCorrespondP = pwDiv.querySelectorAll('p')[3];
let pwPass = false;
let pwCheckPass = false;

const authDiv = document.querySelector("div[id='auth-div']");
const toEmail = authDiv.querySelector('select');
//여기석 select된 대상을 to mail 로 지정해서 보내줘야하니까.
const signUpBtn = document.querySelector('.signup-btn');

/*
email에 대한 정보를 판별
1. 이메일 형식이 맞는가?
2. 사용가능한가? > 서버에 등록되지 않았는가?
*/
emailInput.onchange = ()=>{
    console.log("이메일 **************************")
    //스타일 초기화
    emailWrongP.style.display = 'none';
    emailDuplicateP.style.display = 'none';
    emailSuccessP.style.display = 'none';
    emailPass = false;

    //메일 입력 정보에 대해서 정규식으로 확인.
    //사실상 정규식 통과 못하면 일단 다 쳐내면 되잖아.
    if( !/^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i
        .test(emailInput.value) ){
        console.log("정규식 통과 못함.")
        emailWrongP.style.display = 'block'
        return;
    }
    console.log("조건 통과함.")
    console.log("fetch로 해당 email이 존재하는지 확인")

    emailPass = true; //임시로 여기 두기 아 이거 유니크
    //여기에 메일을 발송하는 코드를 작성하면 된다.

    // fetch(`/authentication/emailcheck?email=${emailInput.value}`)
    //     .then(
    //         response =>{
    //             //값이 어떻게 오는지 확인
    //             let data = response.json();
    //             // console.log("응답 바로 확인 : "+response);
    //             // console.log("파스값 확인 : "+data)
    //             //여긴 성공했을 때 들어가는 곳이야.
    //             if (response.ok && response.status === 200){
    //                 console.log("송신값 성공임.")
    //                 return data;
    //             }
    //             console.log("송신값 실패임.")
    //             emailWrongP.style.display = "block";
    //             //여긴 실패했을때 바로 처리되는 곳이야.
    //             throw new Error();
    //         })
    // .then(value => {
    //     console.log("여기는 왔니?")
    //     if (value){
    //         //true는 값이 존재한다는 뜻이니 사용 불가판정
    //         console.log("받은 응답에 대한 값 : ", value)
    //         emailDuplicateP.style.display = "block";
    //     }else {
    //         //false는 값이 없다는 뜻이니 사용가능 판정
    //         console.log("받은 응답에 대한 값 : ", value)
    //         emailSuccessP.style.display = "block";
    //         emailPass = true;
    //     }
    // });


}


/*
아이디 판별
1. 아이디가 20글자 이내로 영어+숫자의 조합인가
2. 기존에 등록된 아이디가 있는가?
*/
idInput.onchange = ()=>{
    console.log("아이디 **************************")

    //스타일 초기화
    idDuplicateP.style.display = 'none';
    idSuccessP.style.display = 'none';
    idFailP.style.display = 'none';
    idPass = false;

    //메일 입력 정보에 대해서 정규식으로 확인.
    //사실상 정규식 통과 못하면 일단 다 쳐내면 되잖아.
    //글자가 4~20자 사이, 영어, 숫자의 조합.
    if( !/^[a-zA-Z0-9]{4,20}$/i
        .test(idInput.value) ){
        console.log("정규식 통과 못함.")
        idFailP.style.display = 'block'
        return;
    }
    console.log("조건 통과함.")

    console.log("fetch로 해당 아이디가 존재하는지 확인")
    idPass = true;

    //여기에 메일을 발송하는 코드를 작성하면 된다.
    // fetch(`/authentication/idcheck?id=${idInput.value}`)
    //     .then(
    //         response =>{
    //             //값이 어떻게 오는지 확인
    //             // console.log(response.json());
    //             //여긴 성공했을 때 들어가는 곳이야.
    //             if (response.ok && response.status === 200){
    //                 console.log("송신값 성공임.")
    //                 return response.json();
    //                 // return;
    //             }
    //             console.log("송신값 실패임.")
    //             failP.style.display = "block";
    //             //여긴 실패했을때 바로 처리되는 곳이야.
    //             throw new Error();
    //         })
    //     .then(value => {
    //         if (value){
    //             //true는 값이 존재한다는 뜻이니 사용 불가판정
    //             console.log("받은 응답에 대한 값 : ", value)
    //             idDuplicateP.style.display = "block";
    //         }else {
    //             //false는 값이 없다는 뜻이니 사용가능 판정
    //             console.log("받은 응답에 대한 값 : ", value)
    //             idSuccessP.style.display = "block";
    //             idPass = true;
    //         }
    //     });


}

/*
이름 판별
한글 6글자, 영어로는 18자, 빈칸은 안돼
*/
nameInput.onchange= () =>{
    console.log("이름**************************")
    //초기화
    nameFailP.style.display = 'none';
    nameSuccessP.style.display = 'none';
    namePass = false;

    //byte 계산을 위해 textencoder를 호출
    const encoder = new TextEncoder();
    const byteLength = encoder.encode(nameInput.value).length;

    //byteLength에 따라서 판별식을 적용.
    console.log(byteLength)
    if (0< byteLength && byteLength <= 18){
        nameSuccessP.style.display='block';
        namePass = true;
    }else{
        nameFailP.style.display='block';
    }
}


/*
비밀번호 판별
비밀번호가 영어, 숫자, 특수문자를 섞어서 8글자 이상 16글자 이하 조건인가?
비밀번호 확인과 데이터가 동일한가?
 */

pwInput.onchange = () => {
    console.log("비번**************************")
    //값들 초기화
    pwPass = false;
    pwSuccessP.style.display = 'none';
    pwFailP.style.display = 'none';
    pwCorrespondP.style.display = 'none';

    //판별 함수를 통해서 검증
    if (!pwJudge(pwInput.value)) {
        console.log("정규식 검증 실패");
        pwFailP.style.display = 'block';
        return;
    }
        console.log("정규식 검증 성공");
        pwSuccessP.style.display = 'block';
        pwPass = true;


    //비번 확인에 값이 없는 경우는 넘어가기
    // if (pwRepeatInput.value === ''){
    //     return;
    // }

    // //그게 아니면 일치 검증을 해야지
    // if (pwRepeatInput.value === pwInput.value){
    //     //비번 값 일치
    //     pwSuccessP.style.display = 'block';
    //     pwPass = true;
    //     console.log("패스 여부 : ", pwPass);
    //     return;
    // }

    //불일치
    // pwSuccessP.style.display = 'none';
    // pwCorrespondP.style.display = 'block';
    // pwPass = false;
    // console.log("패스 여부 : ", pwPass);

}

pwRepeatInput.onchange=()=>{
    console.log("비번 확인**************************")
    //비번에서 뭐 문제 있으면 얘는 응답없음.
    if (!pwPass){
        console.log("비번 먼저 확인해.")
        return;
    }

    //초기화
    pwSuccessP.style.display = 'none';
    pwFailP.style.display = 'none';
    pwCorrespondP.style.display = 'none';
    pwCheckPass = false;


    if (pwRepeatInput.value === pwInput.value){
        //비번 값 일치
        pwSuccessP.style.display = 'block';
        pwCheckPass = true;
        console.log("패스 여부 : ", pwCheckPass);
        return;
    }

    pwSuccessP.style.display = 'none';
    pwCorrespondP.style.display = 'block';
    console.log("패스 여부 : ", pwCheckPass);
}

//비밀번호를 판별하는 함수
function pwJudge(pwString){
    if (/^[a-zA-Z0-9`~!@#$%^&*()-_=+;:'",<.>/?]{8,16}$/
        .test(pwString)){
        // 불일치 하면
        console.log("정규식 검증 성공");
        return true;
    }
    console.log("정규식 검증 실패");
    return false;
}


/*주소지는 뭐.. 그냥 비어도 되는곳이니까 따로 검증 안함.*/

//회원가입 버튼을 누를 때 pwPass가 켜져 있어야 넘어가게 하면 되겠지?
signUpBtn.onclick = (event)=>{
    console.log("회원가입 **************************")
    //각종 pass값을 확인
    console.log("이메일 점검 : ", emailPass);
    console.log("아이디 점검 : ", idPass);
    console.log("닉네임 점검 : ", namePass);
    console.log("비밀번호 점검 : ", pwPass);

//여긴 입력이 끝나지 않았을 경우 제외 시키는 것.
    if (emailPass === false){
        alert("이메일 주소를 확인해주세요");
        emailInput.focus();
        event.preventDefault();
        return;
    }

    if (idPass === false){
        alert("아이디를 확인해주세요");
        idInput.focus();
        event.preventDefault();
        return;
    }

    if (namePass === false){
        alert("닉네임을 확인해주세요");
        emailInput.focus();
        event.preventDefault();
        return;
    }

    if (pwCheckPass === false){
        alert("비밀번호를 확인해주세요");
        pwInput.focus();
        event.preventDefault();
        return;
    }

    //검증에 이상이 없다면 회원가입을 post로 보내면 된다.
    // 버튼을 여러번 누를 수 없게 만들기.
    signUpBtn.disabled = true;
    signUpBtn.style.backgroundColor = 'gray';
    dataForm.submit();

    // setTimeout(()=>{
    //     signUpBtn.style.backgroundColor = 'black';
    //     signUpBtn.disabled = false;
    // },5000)
    //이벤트의 전달을 중지
    // event.preventDefault();
}

