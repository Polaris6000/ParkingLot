//상부의 분류를 나누는 곳.
const findIdBtn = document.querySelector(".find_id button");
const findPwBtn = document.querySelector(".find_pw button");
//데이터 양식 영역
const findIdDiv = document.querySelector(".find_id_group");
const findPwDiv = document.querySelector(".find_pw_group");
//데이터 입력 영역
const findIdInputEmail = findIdDiv.querySelector("input");
const findPwInputId = findPwDiv.querySelectorAll("input")[0];
const findPwInputEmail = findPwDiv.querySelectorAll("input")[1];
//입력 후 버튼
const findSummitBtn = document.querySelectorAll("button")[2];
let findTarget = "id";
let isPorcessing = false;

//아이디 찾기 화면으로 전환
findIdBtn.onclick = () => {
    findIdDiv.style.display = 'block';
    findIdBtn.style.backgroundColor = '#1f2d3d';
    findIdBtn.style.color = 'white';
    findTarget = 'id';

    findPwDiv.style.display = 'none';
    findPwBtn.style.backgroundColor = 'white';
    findPwBtn.style.color = 'black';
}

//비밀번호 변경 화면으로 전환
findPwBtn.onclick = () => {
    findPwDiv.style.display = 'block';
    findPwBtn.style.backgroundColor = '#1f2d3d';
    findPwBtn.style.color = 'white';
    findTarget = 'pw';

    findIdDiv.style.display = 'none';
    findIdBtn.style.backgroundColor = 'white';
    findIdBtn.style.color = 'black';
}

findSummitBtn.onclick = () => {
    //다중 클릭 방지 대책
    if (isPorcessing) {
        console.log('이미 실행중임.')
        return;
    }
    //현재 작업중임을 표시
    isPorcessing = true;
    findSummitBtn.style.backgroundColor = 'gray';
    findSummitBtn.disabled = true;
    findSummitBtn.textContent = '처리 중';

    //경우별 함수 작동.
    if (findTarget === 'id') {
        console.log('아이디 찾기 로직')
        findId(findIdInputEmail.value);
    } else {
        console.log('비번 찾기 로직')
        changePw(findPwInputId.value, findPwInputEmail.value);
    }

}

function resetBtn(){
    console.log('다시 실행 가능.');
    isPorcessing = false;
    console.log('버튼 활성화');
    findSummitBtn.removeAttribute('disabled');
    findSummitBtn.style.backgroundColor = '#1f2d3d';
    findSummitBtn.textContent = '찾기';
}

//아이디 찾기에 정보를 입력했을 때 처리
function findId(email) {
    // console.log(findIdInputEmail.value)
    if (email === '') {
        alert("빈칸을 입력하셨습니다.");
        resetBtn();
        return;
    }

    // fetch(`http://localhost:8080/authentication/findid?email=${email}`)
    //     .then(response => {
    //             // console.log(response.json()); //왜 이거 있으면 오류 나냐
    //             //일단 값 확인하기
    //             return response.json();
    //         }
    //     ).then(value => {
    //         if (value) {
    //             alert("작성한 메일로 아이디를 발송");
    //         } else {
    //             alert("대상 메일이 없습니다.");
    //         }
    //     }
    // )
}

//비밀 번호 변경에 정보를 입력햇을 때 처리
function changePw(id, email) {
    // console.log(findPwInputId.value)
    // console.log(findPwInputEmail.value)
    if (id === '') {
        alert("아이디칸이 비었습니다.");
        resetBtn();
        return;
    }

    if (email === '') {
        alert("이메일칸이 비었습니다.");
        resetBtn();
        return;
    }


    // fetch(`http://localhost:8080/authentication/changepwmail?id=${id}&email=${email}`)
    //     .then(response => {
    //             // console.log(response.json()); //왜 이거 있으면 오류 나냐
    //             //일단 값 확인하기
    //             return response.json();
    //         }
    //     ).then(value => {
    //         if (value) {
    //             alert("작성한 메일로 아이디를 발송");
    //         } else {
    //             alert("아이디와 이메일을 확인해주세용");
    //         }
    //     }
    // )
}