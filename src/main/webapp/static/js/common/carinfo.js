// 오른쪽 박스 - 주차 상세 정보
// 실시간 날짜, 출차시간, 비용 업데이트

// 현재 날짜를 YY-MM-DD 형식으로 업데이트
function updateDate() {
    const today = new Date();
    const yy = String(today.getFullYear()).slice(2);
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    document.getElementById('today').textContent = `${yy}-${mm}-${dd}`;
}

// 현재 시간을 HH:MM 형식으로 업데이트 (출차 시간)
function updateExitTime() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    document.getElementById('exitTime').textContent = `${hours}:${minutes}`;
}

// 주차 비용 계산
function calculateCost() {
    const infoType = document.getElementById('infoType').textContent;
    const enterTimeStr = document.getElementById('enterTime').textContent;
    const exitTimeStr = document.getElementById('exitTime').textContent;

    if (infoType === 'Monthly') {
        // 월정액 회원은 무료
        document.getElementById('cost').textContent = '0 ₩';
    } else {
        // 시간당 요금 계산 (시간당 3000원)
        const enterParts = enterTimeStr.split(':');
        const exitParts = exitTimeStr.split(':');

        const enterMinutes = parseInt(enterParts[0]) * 60 + parseInt(enterParts[1]);
        const exitMinutes = parseInt(exitParts[0]) * 60 + parseInt(exitParts[1]);

        const durationMinutes = exitMinutes - enterMinutes;
        const durationHours = Math.ceil(durationMinutes / 60);

        const cost = Math.max(0, durationHours * 3000);
        document.getElementById('cost').textContent = `${cost.toLocaleString()} ₩`;
    }
}

// 차량 정보 업데이트 (외부에서 호출 가능)
function updateCarInfo(carData) {
    if (carData.carNumber) {
        document.getElementById('carNumber').textContent = carData.carNumber;
    }
    if (carData.status) {
        document.getElementById('status').textContent = carData.status;
    }
    if (carData.infoType) {
        document.getElementById('infoType').textContent = carData.infoType;
    }
    if (carData.infoSubtitle) {
        document.getElementById('infoSubtitle').textContent = carData.infoSubtitle;
    }
    if (carData.enterTime) {
        document.getElementById('enterTime').textContent = carData.enterTime;
    }

    // 비용 재계산
    calculateCost();
}

// 초기화
function initialize() {
    // 날짜 업데이트
    updateDate();

    // 출차 시간 업데이트
    updateExitTime();

    // 비용 계산
    calculateCost();

    // 1분마다 출차 시간 및 비용 업데이트
    setInterval(() => {
        updateExitTime();
        calculateCost();
    }, 60000); // 60초

    console.log('주차 정보 시스템이 초기화되었습니다.');
}

// 페이지 로드 시 자동 실행
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initialize);
} else {
    initialize();
}

// 전역 함수로 내보내기
window.ParkingInfo = {
    updateCarInfo: updateCarInfo,
    calculateCost: calculateCost
};