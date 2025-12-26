<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>500 — Ошибка сервера</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/templates/style.css"/>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/templates/main.css"/>
</head>
<body>
<div class="page">
    <div class="topbar">
        <div class="brand">
            <div class="logo" aria-hidden="true">🛠️</div>
            <div class="brand-text">
                <div class="brand-title">HitCheck</div>
                <div class="brand-subtitle">Проверка попадания точки</div>
            </div>
        </div>
        <button class="theme-toggle" type="button" aria-label="Переключить тему">
            <span class="theme-toggle-icon" aria-hidden="true">🌙</span>
        </button>
    </div>

    <div class="card">
        <h3>500 — внутренняя ошибка</h3>
        <p class="lead">Что-то пошло не так на стороне сервера. Попробуйте обновить страницу или зайти позже.</p>
        <a class="pill" href="<%= request.getContextPath() %>/">На стартовую</a>
    </div>
</div>

<script src="<%= request.getContextPath() %>/templates/theme.js"></script>
</body>
</html>
