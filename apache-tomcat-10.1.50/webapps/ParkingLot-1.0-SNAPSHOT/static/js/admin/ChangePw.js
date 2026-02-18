// //비밀번호 칸
// const pwInput = document.querySelectorAll("input")[0];
// const pwRepeatInput = document.querySelectorAll("input")[1];
//
// //확인 버튼
// const submitBtn = document.querySelector("button");
// let isPorcessing = false;
// const form = document.querySelector('form');
//
//
// submitBtn.onclick=()=>{
//     // console.log('비밀번호 값 : ',pwInput.value);
//     // console.log('window location값 : ',window.location.search)
//
//     //다중 클릭 방지 대책
//     if (isPorcessing){
//         console.log('이미 실행중임.')
//         return;
//     }
//     //현재 작업중임을 표시
//     isPorcessing = true;
//     console.log('실행에 들어감.')
//
//     //버튼 형태바꿔주기
//     submitBtn.style.backgroundColor = 'gray';
//     submitBtn.disabled = true;
//     submitBtn.textContent = '처리 중';
//
//
//     //토큰값 추출
//     // var tokenAll = window.location.search.replace("?","")
//     // var token = tokenAll.split('=')[1];
//     // console.log(token);
//
//     //비번 변경 요청하기
//     // fetch(`http://localhost:8080/authentication/changepw?token=${token}&pw=${pwInput.value}`);
//     // alert("비밀번호 변경이 완료되었습니다. 로그인해주시기 바랍니다.")
//     setTimeout(() =>{
//         isPorcessing = false;
//         submitBtn.style.backgroundColor = '#1f2d3d';
//         submitBtn.disabled = false;
//         submitBtn.textContent = '확인';
//         console.log('실행끝');
//     },2000);
//
//
// }