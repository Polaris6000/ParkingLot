//상부의 분류를 나누는 곳.
const findIdBtn = document.querySelector(".find-id button");
const findPwBtn = document.querySelector(".find-pw button");
//데이터 양식 영역
const findIdForm = document.querySelector(".find-id-group");
const findPwForm = document.querySelector(".find-pw-group");
//데이터 입력 영역
const findIdInputEmail = findIdForm.querySelector("input");
const findPwInputId = findPwForm.querySelectorAll("input")[0];
const findPwInputEmail = findPwForm.querySelectorAll("input")[1];
//입력 후 버튼
const findSummitBtn = document.querySelectorAll("button")[2];
let findTarget = "id";
let isPorcessing = false;
//2개의 폼 값을 가져오기

//아이디 찾기 화면으로 전환
findIdBtn.onclick = () => {
    findIdForm.style.display = 'block';
    findIdBtn.style.backgroundColor = '#1f2d3d';
    findIdBtn.style.color = 'white';
    findTarget = 'id';

    findPwForm.style.display = 'none';
    findPwBtn.style.backgroundColor = 'white';
    findPwBtn.style.color = 'black';
}

//비밀번호 변경 화면으로 전환
findPwBtn.onclick = () => {
    findPwForm.style.display = 'block';
    findPwBtn.style.backgroundColor = '#1f2d3d';
    findPwBtn.style.color = 'white';
    findTarget = 'pw';

    findIdForm.style.display = 'none';
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
        if (findIdInputEmail.value === '') {
            alert("빈칸을 입력하셨습니다.");
            resetBtn();
            return;
        }

        findIdForm.submit();

    } else {
        console.log('비번 찾기 로직')
        if (findPwInputId.value === '') {
            alert("아이디칸이 비었습니다.");
            resetBtn();
            return;
        }

        if (findPwInputEmail.value === '') {
            alert("이메일칸이 비었습니다.");
            resetBtn();
            return;
        }


        findPwForm.submit();
    }
}

function resetBtn() {
    console.log('다시 실행 가능.');
    isPorcessing = false;
    console.log('버튼 활성화');
    findSummitBtn.removeAttribute('disabled');
    findSummitBtn.style.backgroundColor = '#1f2d3d';
    findSummitBtn.textContent = '찾기';
}