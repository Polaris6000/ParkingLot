<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>스마트주차 반월당점 - 관리 시스템</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            padding: 60px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
        }
        
        .logo {
            font-size: 80px;
            margin-bottom: 20px;
        }
        
        h1 {
            color: #333;
            font-size: 36px;
            margin-bottom: 10px;
        }
        
        .subtitle {
            color: #666;
            font-size: 18px;
            margin-bottom: 40px;
        }
        
        .info-box {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            text-align: left;
        }
        
        .info-box h3 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 18px;
        }
        
        .info-box ul {
            list-style: none;
            padding: 0;
        }
        
        .info-box li {
            color: #666;
            padding: 8px 0;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .info-box li:last-child {
            border-bottom: none;
        }
        
        .info-box li strong {
            color: #333;
            display: inline-block;
            width: 120px;
        }
        
        .menu-buttons {
            display: grid;
            grid-template-columns: 1fr;
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .btn {
            display: block;
            padding: 20px 40px;
            background: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 10px;
            font-size: 18px;
            font-weight: bold;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
        }
        
        .btn:hover {
            background: #5568d3;
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #6c757d;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        .footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #e0e0e0;
            color: #999;
            font-size: 14px;
        }
        
        .status {
            display: inline-block;
            padding: 5px 15px;
            background: #48bb78;
            color: white;
            border-radius: 20px;
            font-size: 12px;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="logo">🅿️</div>
        <h1>스마트주차 반월당점</h1>
        <p class="subtitle">주차 관리 시스템 <span class="status">운영중</span></p>
        
        <div class="info-box">
            <h3>📍 시설 정보</h3>
            <ul>
                <li><strong>주차장 명:</strong> 스마트주차 반월당점</li>
                <li><strong>총 주차면수:</strong> 20면 (A-1 ~ A-20)</li>
                <li><strong>운영 시간:</strong> 24시간 연중무휴</li>
                <li><strong>기본 요금:</strong> 1시간 2,000원</li>
                <li><strong>추가 요금:</strong> 30분당 1,000원</li>
            </ul>
        </div>
        
        <div class="menu-buttons">
            <a href="statistics" class="btn">
                📊 관리자 통계 대시보드
            </a>
            <!-- 
            다른 메뉴를 추가할 수 있습니다:
            <a href="parking-status" class="btn btn-secondary">
                🚗 주차 현황 관리
            </a>
            <a href="members" class="btn btn-secondary">
                👥 월정액 회원 관리
            </a>
            -->
        </div>
        
        <div class="footer">
            <p>© 2024 스마트주차 반월당점. All rights reserved.</p>
            <p style="margin-top: 5px; font-size: 12px;">
                문의: 1588-0000 | 관리자 시스템 v1.0
            </p>
        </div>
    </div>
</body>
</html>
