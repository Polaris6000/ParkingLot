// 1. 주차 데이터 (아이콘 상태 제어)
const parkingSpots = [
    // 1번 구역 (id 1~10)
    { id: 1, row: 1, type: 'car', occupied: true },
    { id: 2, row: 1, type: 'car', occupied: false },
    { id: 3, row: 1, type: 'car', occupied: false },
    { id: 4, row: 1, type: 'car', occupied: true },
    { id: 5, row: 1, type: 'car', occupied: false },
    { id: 6, row: 1, type: 'car', occupied: false },
    { id: 7, row: 1, type: 'car', occupied: true },
    { id: 8, row: 1, type: 'car', occupied: false },
    { id: 9, row: 1, type: 'car', occupied: true },
    { id: 10, row: 1, type: 'car', occupied: false },
    // 2번 구역 (id 11~20)
    { id: 11, row: 2, type: 'car', occupied: false },
    { id: 12, row: 2, type: 'car', occupied: false },
    { id: 13, row: 2, type: 'car', occupied: true },
    { id: 14, row: 2, type: 'car', occupied: false },
    { id: 15, row: 2, type: 'car', occupied: false },
    { id: 16, row: 2, type: 'car', occupied: false },
    { id: 17, row: 2, type: 'car', occupied: true },
    { id: 18, row: 2, type: 'car', occupied: false },
    { id: 19, row: 2, type: 'ev', occupied: true }, // 전기차
    { id: 20, row: 2, type: 'ev', occupied: false }, // 전기차 (비어있음)
];

// 2. 화면에 그리는 함수
function renderDashboard(searchId = null) {
    const row1Container = document.getElementById('row1');
    const row2Container = document.getElementById('row2');

    // 기존 내용 삭제
    row1Container.innerHTML = '';
    row2Container.innerHTML = '';

    parkingSpots.forEach(spot => {
        const spotDiv = document.createElement('div');
        // 클래스 부여 (점유 여부와 차종 구분)
        spotDiv.className = `spot ${spot.occupied ? 'occupied' : ''} ${spot.type === 'ev' ? 'ev-spot' : ''}`;

        // 검색어와 일치하면 강조 클래스 추가
        if (searchId && spot.id === parseInt(searchId)) {
            spotDiv.classList.add('search-hit');
        }

        // 아이콘 선택: 전기차는 충전소, 일반차는 자동차
        const iconClass = spot.type === 'ev' ? 'fa-charging-station' : 'fa-car';
        spotDiv.innerHTML = `<i class="fa-solid ${iconClass}"></i>`;

        // 행에 맞춰서 집어넣기
        if (spot.row === 1) row1Container.appendChild(spotDiv);
        else row2Container.appendChild(spotDiv);
    });

    // 상단 카드 수치 갱신
    updateStats();
}

// 3. 상단 통계 업데이트 함수
function updateStats() {
    const totalOccupied = parkingSpots.filter(s => s.occupied).length;
    const evOccupied = parkingSpots.filter(s => s.type === 'ev' && s.occupied).length;

    document.getElementById('occupancy-rate').innerText = `${totalOccupied}/${parkingSpots.length}`;
    document.getElementById('ev-rate').innerText = `${evOccupied}/2`;
}

// 4. 검색창 입력 이벤트 전파
document.getElementById('searchInput').addEventListener('input', (e) => {
    renderDashboard(e.target.value);
});

// 페이지 로드 시 최초 실행
renderDashboard();