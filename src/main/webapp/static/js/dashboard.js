/*
변화가 생기는 부분 : 약식 통계

차량 주차 부분

오른쪽 정산 부분

정산하기 버튼 부분
 */
//약식 통계 >> 이건 자바에서 바로 정보 받아서 처리
// const statisticsArea = document.querySelector('.stats-container');
// //이건 정보를 받아와서 약식으로 통계를 내야하니까 ajax를 쓰면 되는거고.
// const currentCount = statisticsArea.querySelector('#currentParkedCount');
// const availableCount = statisticsArea.querySelector('#availableSpots');
// const todayCount = statisticsArea.querySelector('#todayVisitorCount');
// const valiPercent = statisticsArea.querySelector('#occupancyRate');

//주차 현황 부분
const parkingArea = document.querySelector('div.occupancy-section');
//여긴 버튼 클릭하면 오른쪽 입력부분이 바뀌어야 하니까 그것을 이용
const parkingSpot = parkingArea.querySelectorAll('div.spot');

//입력 부분 form의 action을 바꿔서 submit 하는 식으로 기능.
const right_infoForm = document.querySelector('div.right-panel > form.info-box');
const idInput_r = right_infoForm.querySelector('#id');
const dateBackUp_r = right_infoForm.querySelector('#enterTimeBackUp');
const payTimeInput_r = right_infoForm.querySelector('#payTime');
const date_r = right_infoForm.querySelector('#today'); //날짜 인풋
const carNumber_r = right_infoForm.querySelector('#carNumber'); //차량 번호
const parkingStat_r = right_infoForm.querySelector('#status'); // 차량 상태(입차, 출차)
const discountInfo_r = right_infoForm.querySelector('#discount'); // 할인 정보 : 일반/경차/장애인/월주차
const enterTime_r = right_infoForm.querySelector('#enterTime'); // 입차 시간
const exitTime_r = right_infoForm.querySelector('#exitTime'); // 출차 시간
const cost_r = right_infoForm.querySelector('#cost'); //주차 비용

let normalCost = 0; //기준값이 되는 비용.

//정산하기 버튼
const moneyBtn = document.querySelector(".dashboard-header").querySelector('button');
//여길 누르면 입력부분에 있던 데이터들을 가져와서 모달창을 띄워야 한다.

//초기화 작업
const parkingJSON = document.querySelector('div#jsonData-car').textContent;
const feeJSON = document.querySelector('div#jsonData-fee').textContent;
// console.log(parkingJSON);

const parkingData = parkingJSON ? JSON.parse(parkingJSON) : [];
const feeData = feeJSON ? JSON.parse(feeJSON) : [];

//모달창 값 불러오기
const modalDiv = document.querySelector('div.modal');
const modal_infoForm = modalDiv.querySelector('form.info-box');
const idInput_m = modal_infoForm.querySelector('#id');
const dateBackUp_m = modal_infoForm.querySelector('#enterTimeBackUp');
const payTimeInput_m = modal_infoForm.querySelector('#payTime');
const date_m = modal_infoForm.querySelector('#today'); //날짜 인풋
const carNumber_m = modal_infoForm.querySelector('#carNumber'); //차량 번호
const parkingStat_m = modal_infoForm.querySelector('#status'); // 차량 상태(입차, 출차)
const discountInfo_m = modal_infoForm.querySelector('#discount'); // 할인 정보 : 일반/경차/장애인/월주차
const enterTime_m = modal_infoForm.querySelector('#enterTime'); // 입차 시간
const exitTime_m = modal_infoForm.querySelector('#exitTime'); // 출차 시간
const cost_m = modal_infoForm.querySelector('#cost'); //주차 비용

const submit_modal_btn =modalDiv.querySelectorAll('button')[0]
const cancle_modal_btn =modalDiv.querySelectorAll('button')[1]


/*****************************************************************************/
//초기화
parkingSpot.forEach(item => {
    item.onclick = enterProcess;
})

parkingData.forEach(item => {
    // console.log("주차 ID:", item.id);
    const spot = Number(item.parkingSpot.replaceAll("A", ''));
    // console.log(spot);
    // console.log(parkingSpot[spot - 1].querySelector('div').textContent);
    //저 값으로 이제 색상을 찾으면 되겠네?
    parkingSpot[spot - 1].classList.add('occupied');
    // parkingSpot[spot-1].classList.remove('occupied')
    //얘들은 onclick을 갱신해주면 되겠다.
    parkingSpot[spot - 1].onclick = exitProcess;
});
/*****************************************************************************/

//할인정보가 변경되면 자동으로 비용 계산 다시 해줘.
discountInfo_r.onchange = () =>{
    discountCost(discountInfo_r.value)
};

//빈 차량 부분을 선택했을 때 일어날 일.
function enterProcess(event) {
    //왼쪽의 버튼을 클리갛면 오른쪽 데이터들을 채워주는 역할
    //입차 처리 중
    const now = new Date();
    //날짜 저장.
    const day = now.getFullYear() + "-" + ("" + (now.getMonth() + 1)).padStart(2, "0") + "-" + ("" + now.getDay()).padStart(2, "0");
    //시간 저장
    // console.log(now);
    const time = now.getHours() + ":" + ("" + (now.getMinutes() + 1)).padStart(2, "0");

    idInput_r.value = "";
    dateBackUp_r.value = now;
    date_r.value = day;
    carNumber_r.value = ""; //공백으로 초기화. + 자동 포커스
    carNumber_r.setAttribute("autofocus", "");
    carNumber_r.removeAttribute("readonly"); //수정할 수 있게 바꾸기.
    parkingStat_r.value = "입차(" + event.currentTarget.getAttribute('data-spot') + ")";
    discountInfo_r.value = "normal";
    enterTime_r.value = time;
    exitTime_r.value = "";
    cost_r.value = "0원";

    //버튼 활성화 및 이름 바꾸기
    moneyBtn.removeAttribute('disabled');
    moneyBtn.removeAttribute('style');
    moneyBtn.removeAttribute('cursor');
    moneyBtn.innerHTML = `<i class="fas fa-sign-out-alt"></i> <span>입차처리</span>`;
}

//데이터가 있는 곳을 선택했을 때 생기는 일.
function exitProcess(event) {
    //왼쪽의 버튼을 클리갛면 오른쪽 데이터들을 채워주는 역할
    //출차 처리 중
    //지금 클릭된 곳의 위치를 알아야해. node정보가 필요하다.
    // console.log(event.currentTarget);
    // console.log(parkingData[findSpot(event.currentTarget.getAttribute('data-spot'))]);
    // console.log(feeData);

    //현재 대상을 지정.
    const thisTarget = parkingData[findSpot(event.currentTarget.getAttribute('data-spot'))];
    //입차시간을 받아오기.
    const entryTime = thisTarget.entryTime;

    //현재 시간을 확인.
    const now = new Date();
    // console.log(now)
    //날짜 저장.
    const day = now.getFullYear() + "-" + ("" + (now.getMonth() + 1)).padStart(2, "0") + "-" + ("" + now.getDay()).padStart(2, "0");
    //시간 저장
    const time = now.getHours() + ":" + ("" + (now.getMinutes() + 1)).padStart(2, "0");

    //input 값들을 설정.
    id.value = thisTarget.id
    dateBackUp_r.value = entryTime;
    payTimeInput_r.value = day + 'T' + time + ':'+now.getSeconds();
    date_r.value = entryTime.split(' ')[0];
    carNumber_r.value = thisTarget.plateNumber; //수정못하게 readonly추가
    carNumber_r.setAttribute("readonly", "");
    carNumber_r.setAttribute("autofocus", "");


    //시간에 대한 값을 지정.
    const allTime = entryTime.split(' ')[1];
    [hr, min, sec] = allTime.split(':');
    enterTime_r.value = hr + ":" + min;
    exitTime_r.value = time;

    //시간 차이 계산
    const enterMin = hr * 60 + min * 1;
    const exiteMin = time.split(':')[0] * 60 + time.split(':')[1] * 1;

    const payTime = exiteMin - enterMin;

    //회차 시간 계산하지
    if (payTime <= feeData.gracePeriodMinutes) {
        parkingStat_r.value = "회차(" + event.currentTarget.getAttribute('data-spot') + ")";
        normalCost = 0;
    } else {
        parkingStat_r.value = "입차(" + event.currentTarget.getAttribute('data-spot') + ")";
        if (payTime <= feeData.basicUnitMinute) {
            normalCost = feeData.baseFee;
        } else {
            normalCost = feeData.baseFee + Math.ceil((payTime - feeData.basicUnitMinute) / feeData.billingUnitMinutes) * feeData.unitFee
        }
    }
    discountCost(thisTarget.kindOfDiscount);


    //비용과 관련된 부분을 설정.

    //버튼 활성화 및 이름 바꾸기
    moneyBtn.removeAttribute('disabled');
    moneyBtn.removeAttribute('style');
    moneyBtn.removeAttribute('cursor');
    moneyBtn.innerHTML = `<i class="fas fa-sign-out-alt"></i> <span>출차처리</span>`;

}

//할인에 따른 정산 갱신
function discountCost(kindOfDiscount){
    // console.log(kindOfDiscount) //받은값 확인    //할인율 반영
    switch (kindOfDiscount) {
        case "normal" :
            discountInfo_r.value = "normal";
            cost_r.value = normalCost;
            break;
        case "disabled" :
            discountInfo_r.value = "disabled";
            cost_r.value = Math.ceil(normalCost * (100 - feeData.helpDiscountRate) / 100 / 100) * 100 //십의자리 절삭을 위해 100으로 나고 다시 곱.
            break;
        case "light" :
            discountInfo_r.value = "light";
            cost_r.value = Math.ceil(normalCost * (100 - feeData.compactDiscountRate) / 100 / 100) * 100
            break;
        case "monthly" :
            discountInfo_r.value = "monthly";
            cost_r.value = 0;
            break;
    }
    cost_r.value += "원";
}

//장소의 정보를 찾는 함수
function findSpot(spot) {
    //문자열을 찾아서 해당하는 배열의 인덱스 반환하기.
    for (let i = 0; i < parkingData.length; i++) {
        // console.log(parkingData[i].parkingSpot);
        if (parkingData[i].parkingSpot === spot) {
            return i;
        }
    }

    // console.log(spot);
    // console.log(parkingData[0].parkingSpot);
    //
    // return 1;
    return -1;
}

//위쪽 버튼 클릭했을 때 실행할 함수들
moneyBtn.onclick = () => {
    //내부 글자에 따라 진행하기
    const word = moneyBtn.querySelector('span');
    // console.log(word.textContent);
    switch (word.textContent) {
        case '입차처리':
            // console.log("입차내요");
            //액션 주소 바꿔주기
            right_infoForm.setAttribute("action", '/dashboard/enter');
            //서밋해서 데이터 보내기
            right_infoForm.submit();
            break;
            //우선 출차처리는 만들어 놓고, 이걸 옮기는거임. 모달로.
        case '출차처리':
            //여긴 모달창을 띄우고, 모달창에서 해당 코드가 실행ㅎ해야함.
            console.log("출차내요");
            //데이터 복사하기 전에 한번 더 갱신
            modalDataInput(); //데이터를 복사하기.
            modalDiv.style.display = 'block';
            //모달창이 보이게 하면 됌.

            break;
    }

}

submit_modal_btn.onclick = ()=>{
    //액션 주소 바꿔주기
    modal_infoForm.setAttribute("action", '/dashboard/exit');
    //서밋해서 데이터 보내기
    modal_infoForm.submit();
}
cancle_modal_btn.onclick = () =>{
    modalDiv.style.display = 'none';
}

// document.addEventListener("click",(event) =>{
//     console.log(event.currentTarget);
// })

//모달창 설정 관련
//데이터 복사를 하는 함수
function modalDataInput() {
    idInput_m.value =idInput_r.value
    dateBackUp_m.value =dateBackUp_r.value
    payTimeInput_m.value = payTimeInput_r.value
    date_m .value=date_r .value
    carNumber_m .value=carNumber_r .value
    parkingStat_m.value=parkingStat_r.value
    discountInfo_m.value=discountInfo_r.value
    enterTime_m .value=enterTime_r .value
    exitTime_m.value=exitTime_r.value
    cost_m.value=cost_r.value
}
