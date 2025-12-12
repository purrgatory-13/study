<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.model.Point" %>
<%@ page import="com.example.utils.ResultsStorage" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Веб ЛР2</title>
    <link rel="stylesheet" href="form.css">
</head>
<body>

<% if (request.getAttribute("error") != null) { %>
<div class="notification error">
    <%= request.getAttribute("error") %>
</div>
<% } %>

<header class="header">
    <h1>Лабораторная работа №2</h1>
    <div class="student-info">
        ФИО: Пингачева Л.С.  | Группа: P3218 | Вариант: 18324
    </div>
</header>

<div class="container">
    <section class="main-section">
        <div class="graph-container">
            <h2>Область попадания</h2>
            <div class="graph" id="graph">
                <canvas id="graphCanvas" width="500" height="500"></canvas>
            </div>
        </div>
        <div class="history-section">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;">
                <h2>История проверок</h2>
                <button id="clearHistoryBtn" class="clear-btn">Очистить историю</button>
            </div>
            <div class="table-container">
                <table class="results-table">
                    <thead>
                    <tr>
                        <th>X</th>
                        <th>Y</th>
                        <th>R</th>
                        <th>Результат</th>
                        <th>Время запроса</th>
                        <th>Время работы</th>
                    </tr>
                    </thead>
                    <tbody id="resultsBody">
                    <%
                        List<Point> history = ResultsStorage.getResults(application);
                        if (history != null && !history.isEmpty()) {
                            for (int i = history.size() - 1; i >= 0; i--) {
                                Point point = history.get(i);
                    %>
                    <tr>
                        <td><%= point.getX() %></td>
                        <td><%= point.getY() %></td>
                        <td><%= point.getR() %></td>
                        <%
                            String resultClass = point.isHit() ? "result-hit" : "result-miss";
                            String resultText = point.isHit() ? "Попадание" : "Промах";
                        %>
                        <td class="<%= resultClass %>"><%= resultText %></td>
                        <td><%= point.getRequestTime() %></td>
                        <td><%= point.getExecutionTime() %>с</td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="6" class="no-results">Нет данных</td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>

    </section>
    <aside class="parameters-section">
        <h2>Параметры точки</h2>
        <form id="pointForm" action="controller" method="POST">
            <div class="parameter-group">
                <label for="x" class="parameter-label">Координата X:</label>
                <select id="x" name="x" class="select-input" required>
                    <option value="">Выберите X</option>
                    <option value="-3">-3</option>
                    <option value="-2">-2</option>
                    <option value="-1">-1</option>
                    <option value="0">0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
                <div class="validation-message" id="xError">Выберите значение X</div>
            </div>

            <div class="parameter-group">
                <label for="y" class="parameter-label">Координата Y:</label>
                <input type="text" id="y" name="y" class="text-input"
                       placeholder="Введите число от -3 до 3" required>
                <div class="validation-message" id="yError">Y должен быть числом от -3 до 3</div>
            </div>

            <div class="parameter-group">
                <label class="parameter-label">Радиус R:</label>
                <div class="radio-group" id="rRadioGroup">
                    <div class="radio-row">
                        <input type="radio" id="r1" name="r" value="1">
                        <label for="r1">1</label>

                        <input type="radio" id="r1_5" name="r" value="1.5">
                        <label for="r1_5">1.5</label>

                        <input type="radio" id="r2" name="r" value="2">
                        <label for="r2">2</label>

                        <input type="radio" id="r2_5" name="r" value="2.5">
                        <label for="r2_5">2.5</label>

                        <input type="radio" id="r3" name="r" value="3">
                        <label for="r3">3</label>
                    </div>
                </div>
                <div class="validation-message" id="rError">Выберите значение R</div>
            </div>

            <button type="submit" class="submit-btn" id="submitBtn">Проверить попадание</button>
        </form>
    </aside>

</div>

<footer class="footer">
    <div class="footer-content">
        <p>*место под пропаганду ТФ анимейтед*</p>
        <p>2025</p>
    </div>
</footer>

<script src="formscript.js"></script>
</body>
</html>