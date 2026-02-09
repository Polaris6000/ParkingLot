<!DOCTYPE html>
<html lang="ko">
<head>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
    <title>스마트주차 관리자 대시보드</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        :root { --primary: #2c3e50; --accent: #3498db; }
        body { font-family: 'Segoe UI', sans-serif; background: #f4f7f6; margin: 0; padding: 20px; }
        .container { max-width: 1200px; margin: 0 auto; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h2 { color: var(--primary); border-left: 5px solid var(--accent); padding-left: 10px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .btn { background: var(--accent); color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
        .btn:hover { opacity: 0.9; }
    </style>
</head>
<body>

<div class="container">
    <h1>📊 주차 관리 시스템 대시보드</h1>

    <div class="grid">
        <div class="card">
            <h2>일별 매출 현황</h2>
            <canvas id="dailySalesChart"></canvas>
        </div>
        <div class="card">
            <h2>차종별 이용 비율</h2>
            <canvas id="typePieChart"></canvas>
        </div>
    </div>

    <div class="card">
        <h2>주차 요금 정책 설정</h2>
        <form id="policyForm">
            <div class="grid" style="grid-template-columns: repeat(3, 1fr);">
                <div class="form-group">
                    <label>기본 요금 (원)</label>
                    <input type="number" name="base_fee" value="2000">
                </div>
                <div class="form-group">
                    <label>추가 단위 요금 (원)</label>
                    <input type="number" name="unit_fee" value="1000">
                </div>
                <div class="form-group">
                    <label>단위 시간 (분)</label>
                    <input type="number" name="billing_unit_minutes" value="30">
                </div>
                <div class="form-group">
                    <label>일일 최대 요금 (원)</label>
                    <input type="number" name="max_cap_amount" value="15000">
                </div>
                <div class="form-group">
                    <label>경차 할인율 (%)</label>
                    <input type="number" name="compact_discount_rate" value="30">
                </div>
                <div class="form-group">
                    <label>장애인 할인율 (%)</label>
                    <input type="number" name="help_discount_rate" value="50">
                </div>
            </div>
            <button type="button" class="btn" onclick="updatePolicy()">정책 업데이트 저장</button>
        </form>
    </div>
</div>

<script>
    // 페이지 로드 시 차트 초기화
    document.addEventListener('DOMContentLoaded', function() {
        initDailyChart();
        initPieChart();
    });

    // 1. 매출 선형 차트 (Bar Chart)
    function initDailyChart() {
        const ctx = document.getElementById('dailySalesChart').getContext('2d');
        // 실제 구현 시 JSP/Servlet에서 데이터를 JSON으로 넘겨받아야 함
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['02-05', '02-06', '02-07', '02-08', '02-09'],
                datasets: [{
                    label: '매출액(원)',
                    data: [45000, 52000, 38000, 61000, 49000],
                    backgroundColor: '#3498db'
                }]
            }
        });
    }

    // 2. 차종 비율 차트 (Pie Chart)
    function initPieChart() {
        const ctx = document.getElementById('typePieChart').getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['일반', '경차', '장애인', '월정액'],
                datasets: [{
                    data: [60, 20, 10, 10],
                    backgroundColor: ['#2c3e50', '#e74c3c', '#f1c40f', '#2ecc71']
                }]
            }
        });
    }

    // 3. 정책 업데이트 (AJAX 사용)
    function updatePolicy() {
        const formData = new FormData(document.getElementById('policyForm'));
        const data = Object.fromEntries(formData.entries());

        // Fetch API를 이용해 서블릿에 데이터 전송
        fetch('/admin/updatePolicy', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(response => {
                if(response.ok) alert('요금 정책이 성공적으로 변경되었습니다.');
                else alert('저장에 실패했습니다.');
            })
            .catch(err => console.error('Error:', err));
    }
</script>

</body>
</html>