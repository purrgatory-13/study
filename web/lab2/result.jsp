<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.model.Point" %>
<%@ page import="com.example.utils.ResultsStorage" %>
<%@ page import="java.util.List" %>
<%
    Point point = (Point) request.getAttribute("point");
    if (point == null) {
        response.sendRedirect("controller");
        return;
    }

    // Получаем историю результатов
    List<Point> history = ResultsStorage.getResults(application);
    int recentCount = Math.min(5, history != null ? history.size() : 0); // Последние 5 результатов
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="result.css">
</head>
<body>

<header class="header">
    <h1>Результат</h1>
    <div class="student-info">
        ФИО: Пингачева Л.С. | Группа: P3218 | Вариант: 18324
    </div>
</header>

<div class="container">
    <!-- Секция с текущим результатом -->
    <section class="result-section">
        <h2>Текущий результат</h2>
        <div class="table-container">
            <table class="result-table">
                <thead>
                <tr>
                    <th>Параметр</th>
                    <th>Значение</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Координата X</td>
                    <td><%= point.getX() %></td>
                </tr>
                <tr>
                    <td>Координата Y</td>
                    <td><%= point.getY() %></td>
                </tr>
                <tr>
                    <td>Радиус R</td>
                    <td><%= point.getR() %></td>
                </tr>
                <tr>
                    <td>Результат</td>
                    <td class="<%= point.isHit() ? "hit" : "miss" %>">
                        <%= point.isHit() ? "ПОПАДАНИЕ" : "ПРОМАХ" %>
                    </td>
                </tr>
                <tr>
                    <td>Время выполнения</td>
                    <td><%= point.getExecutionTime() %>с</td>
                </tr>
                <tr>
                    <td>Время запроса</td>
                    <td><%= point.getRequestTime() %></td>
                </tr>
                </tbody>
            </table>
        </div>
    </section>


    <!-- Навигация -->
    <div class="navigation-container">
        <a href="controller" class="nav-btn">Проверить другую точку</a>
    </div>
</div>

<footer class="footer">
    <div class="footer-content">
        <p>*место под пропаганду ТФ анимейтед*</p>
        <p>2025</p>
    </div>
</footer>

</body>
</html>