<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Font Awesome CSS 확인하기</title>
    <link rel="stylesheet" type="text/css" href="font-awesome.css">
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; background-color: #f8f9fa; }
        h1 { border-bottom: 2px solid #333; padding-bottom: 10px; }
        .section { margin-bottom: 40px; }
        .icon-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 15px; }
        .icon-item { background: white; padding: 15px; border-radius: 8px; text-align: center; border: 1px solid #ddd; }
        .icon-item i { font-size: 2em; display: block; margin-bottom: 10px; color: #333; }
        .icon-name { font-size: 0.8em; color: #666; word-break: break-all; }
        .badge { display: inline-block; padding: 3px 8px; background: #007bff; color: white; border-radius: 4px; font-size: 0.7em; margin-bottom: 5px; }
    </style>
</head>
<body>

    <h1>Font Awesome 6.4.0 스타일 확인</h1>

    <div class="section">
        <h2>1. 크기 별 스타일 (Sizes)</h2>
        <div class="icon-grid">
            <div class="icon-item"><i class="fa-solid fa-house fa-2xs"></i><span class="icon-name">fa-2xs</span></div>
            <div class="icon-item"><i class="fa-solid fa-house fa-xs"></i><span class="icon-name">fa-xs</span></div>
            <div class="icon-item"><i class="fa-solid fa-house fa-sm"></i><span class="icon-name">fa-sm</span></div>
            <div class="icon-item"><i class="fa-solid fa-house fa-lg"></i><span class="icon-name">fa-lg</span></div>
            <div class="icon-item"><i class="fa-solid fa-house fa-xl"></i><span class="icon-name">fa-xl</span></div>
            <div class="icon-item"><i class="fa-solid fa-house fa-2xl"></i><span class="icon-name">fa-2xl</span></div>
        </div>
    </div>

    <div class="section">
        <h2>2. 애니메이션 효과 (Animations)</h2>
        <div class="icon-grid">
            <div class="icon-item"><i class="fa-solid fa-sync fa-spin"></i><span class="icon-name">fa-spin</span></div>
            <div class="icon-item"><i class="fa-solid fa-heart fa-beat"></i><span class="icon-name">fa-beat</span></div>
            <div class="icon-item"><i class="fa-solid fa-bell fa-shake"></i><span class="icon-name">fa-shake</span></div>
            <div class="icon-item"><i class="fa-solid fa-circle-exclamation fa-fade"></i><span class="icon-name">fa-fade</span></div>
            <div class="icon-item"><i class="fa-solid fa-envelope fa-bounce"></i><span class="icon-name">fa-bounce</span></div>
            <div class="icon-item"><i class="fa-solid fa-star fa-flip"></i><span class="icon-name">fa-flip</span></div>
        </div>
    </div>

    <div class="section">
        <h2>3. 주요 아이콘 샘플</h2>
        <p>CSS 파일에 포함된 클래스들 중 일부입니다.</p>
        <div class="icon-grid">
            <div class="icon-item"><i class="fa-solid fa-trash-can"></i><span class="badge">Solid</span><span class="icon-name">fa-trash-can</span></div>
            <div class="icon-item"><i class="fa-solid fa-message"></i><span class="badge">Solid</span><span class="icon-name">fa-message</span></div>
            <div class="icon-item"><i class="fa-solid fa-file-lines"></i><span class="badge">Solid</span><span class="icon-name">fa-file-lines</span></div>
            <div class="icon-item"><i class="fa-solid fa-calendar-days"></i><span class="badge">Solid</span><span class="icon-name">fa-calendar-days</span></div>
            <div class="icon-item"><i class="fa-solid fa-user-check"></i><span class="badge">Solid</span><span class="icon-name">fa-user-check</span></div>
            <div class="icon-item"><i class="fa-solid fa-magnifying-glass"></i><span class="badge">Solid</span><span class="icon-name">fa-magnifying-glass</span></div>

            <div class="icon-item"><i class="fa-brands fa-facebook"></i><span class="badge" style="background:#3b5998;">Brands</span><span class="icon-name">fa-facebook</span></div>
            <div class="icon-item"><i class="fa-brands fa-github"></i><span class="badge" style="background:#333;">Brands</span><span class="icon-name">fa-github</span></div>
        </div>
    </div>

</body>
</html>