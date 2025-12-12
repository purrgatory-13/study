let currentR = null;
let pointsHistory = [];
let isInitialized = false;

const GRAPH_CONFIG = {
    width: 500,
    height: 500,
    padding: 60,
    axisColor: '#00f3ff',
    gridColor: 'rgba(100, 100, 150, 0.3)',
    pointRadius: 5,
    xMin: -5,
    xMax: 5,
    yMin: -5,
    yMax: 5
};

document.addEventListener('DOMContentLoaded', function() {
    if (!isInitialized) {
        initializePage();
        isInitialized = true;
    }
});

function initializePage() {
    setupEventListeners();
    initializeGraph();
    loadHistoryPoints();

    setTimeout(() => {
        redrawGraph();
    }, 100);
}

function loadHistoryPoints() {
    const tableRows = document.querySelectorAll('.results-table tbody tr');
    pointsHistory = [];

    tableRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        // Исправлено: проверяем, что это не строка "Нет данных"
        if (cells.length >= 6 && !row.querySelector('.no-results')) {
            const point = {
                x: parseFloat(cells[0].textContent),
                y: parseFloat(cells[1].textContent),
                r: parseFloat(cells[2].textContent),
                hit: cells[3].classList.contains('result-hit'),
                requestTime: cells[4].textContent,
                executionTime: cells[5].textContent.replace('с', '') // Убираем 'с' для consistency
            };
            pointsHistory.push(point);
        }
    });

    console.log('Loaded history points:', pointsHistory);
}

function setupEventListeners() {
    // Обработчики для радиокнопок R
    const rRadios = document.querySelectorAll('input[name="r"]');
    rRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            currentR = parseFloat(this.value);
            redrawGraph();
        });
    });

    // Обработчик для формы
    const form = document.getElementById('pointForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            const isXValid = validateX();
            const isYValid = validateY();
            const isRValid = validateR();

            if (!isXValid || !isYValid || !isRValid) {
                e.preventDefault();
                showNotification('Исправьте ошибки в форме', 'error');
            }
        });
    }

    // Обработчик для кнопки очистки истории
    const clearBtn = document.getElementById('clearHistoryBtn');
    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            clearHistory();
        });
    }
}

function initializeGraph() {
    const canvas = document.getElementById('graphCanvas');
    if (!canvas) return;

    canvas.width = GRAPH_CONFIG.width;
    canvas.height = GRAPH_CONFIG.height;

    canvas.addEventListener('click', function(event) {
        if (!currentR) {
            showNotification('Сначала выберите радиус R!', 'error');
            return;
        }

        const rect = canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        const mathX = pixelToMathX(x);
        const mathY = pixelToMathY(y);

        const clampedX = Math.max(-5, Math.min(5, mathX));
        const clampedY = Math.max(-5, Math.min(5, mathY));

        sendPointFromGraph(clampedX, clampedY);
    });

    redrawGraph();
}

function calculateScale() {
    const availableWidth = GRAPH_CONFIG.width - 2 * GRAPH_CONFIG.padding;
    const availableHeight = GRAPH_CONFIG.height - 2 * GRAPH_CONFIG.padding;

    const scaleX = availableWidth / (GRAPH_CONFIG.xMax - GRAPH_CONFIG.xMin);
    const scaleY = availableHeight / (GRAPH_CONFIG.yMax - GRAPH_CONFIG.yMin);

    return Math.min(scaleX, scaleY);
}

function mathToPixelX(mathX) {
    const scale = calculateScale();
    return GRAPH_CONFIG.padding + (mathX - GRAPH_CONFIG.xMin) * scale;
}

function mathToPixelY(mathY) {
    const scale = calculateScale();
    return GRAPH_CONFIG.height - GRAPH_CONFIG.padding - (mathY - GRAPH_CONFIG.yMin) * scale;
}

function pixelToMathX(pixelX) {
    const scale = calculateScale();
    return GRAPH_CONFIG.xMin + (pixelX - GRAPH_CONFIG.padding) / scale;
}

function pixelToMathY(pixelY) {
    const scale = calculateScale();
    return GRAPH_CONFIG.yMax - (pixelY - GRAPH_CONFIG.padding) / scale;
}

function redrawGraph() {
    const canvas = document.getElementById('graphCanvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawCoordinateSystem(ctx);

    if (currentR) {
        drawArea(ctx, currentR);
    }

    drawHistoryPoints(ctx);
}

function drawCoordinateSystem(ctx) {
    ctx.fillStyle = 'rgba(25, 25, 60, 0.95)';
    ctx.fillRect(0, 0, GRAPH_CONFIG.width, GRAPH_CONFIG.height);

    // Сетка
    ctx.strokeStyle = GRAPH_CONFIG.gridColor;
    ctx.lineWidth = 0.5;

    for (let x = GRAPH_CONFIG.xMin; x <= GRAPH_CONFIG.xMax; x++) {
        const pixelX = mathToPixelX(x);
        ctx.beginPath();
        ctx.moveTo(pixelX, GRAPH_CONFIG.padding);
        ctx.lineTo(pixelX, GRAPH_CONFIG.height - GRAPH_CONFIG.padding);
        ctx.stroke();
    }

    for (let y = GRAPH_CONFIG.yMin; y <= GRAPH_CONFIG.yMax; y++) {
        const pixelY = mathToPixelY(y);
        ctx.beginPath();
        ctx.moveTo(GRAPH_CONFIG.padding, pixelY);
        ctx.lineTo(GRAPH_CONFIG.width - GRAPH_CONFIG.padding, pixelY);
        ctx.stroke();
    }

    // Координатные оси
    ctx.strokeStyle = GRAPH_CONFIG.axisColor;
    ctx.lineWidth = 2;

    const xAxisY = mathToPixelY(0);
    ctx.beginPath();
    ctx.moveTo(GRAPH_CONFIG.padding, xAxisY);
    ctx.lineTo(GRAPH_CONFIG.width - GRAPH_CONFIG.padding, xAxisY);
    ctx.stroke();

    const yAxisX = mathToPixelX(0);
    ctx.beginPath();
    ctx.moveTo(yAxisX, GRAPH_CONFIG.padding);
    ctx.lineTo(yAxisX, GRAPH_CONFIG.height - GRAPH_CONFIG.padding);
    ctx.stroke();

    drawArrow(ctx, GRAPH_CONFIG.width - GRAPH_CONFIG.padding, xAxisY, 8, 0);
    drawArrow(ctx, yAxisX, GRAPH_CONFIG.padding, 8, -Math.PI / 2);

    drawLabels(ctx);
}

function drawArrow(ctx, x, y, size, angle) {
    ctx.save();
    ctx.translate(x, y);
    ctx.rotate(angle);

    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(-size, -size / 2);
    ctx.lineTo(-size, size / 2);
    ctx.closePath();
    ctx.fillStyle = GRAPH_CONFIG.axisColor;
    ctx.fill();

    ctx.restore();
}

function drawLabels(ctx) {
    ctx.fillStyle = GRAPH_CONFIG.axisColor;
    ctx.font = '14px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    for (let i = -5; i <= 5; i++) {
        if (i === 0) continue;

        const pixelX = mathToPixelX(i);
        const pixelY = mathToPixelY(0);
        ctx.fillText(i.toString(), pixelX, pixelY + 20);

        const pixelX2 = mathToPixelX(0);
        const pixelY2 = mathToPixelY(i);
        ctx.fillText(i.toString(), pixelX2 - 20, pixelY2);
    }

    ctx.fillText('0', mathToPixelX(0) - 12, mathToPixelY(0) + 15);
    ctx.fillText('X', GRAPH_CONFIG.width - GRAPH_CONFIG.padding + 25, mathToPixelY(0));
    ctx.fillText('Y', mathToPixelX(0), GRAPH_CONFIG.padding - 25);
}

function drawArea(ctx, r) {
    const radius = parseFloat(r);
    const areaColor = 'rgba(0, 243, 255, 0.3)';

    ctx.fillStyle = areaColor;
    ctx.strokeStyle = '#00f3ff';
    ctx.lineWidth = 2;

    const centerX = mathToPixelX(0);
    const centerY = mathToPixelY(0);
    const scale = calculateScale();

    // Первая четверть (x >= 0, y >= 0) - четверть круга радиусом R/2
    const quarterRadius = (radius / 2) * scale;
    ctx.beginPath();
    ctx.arc(centerX, centerY, quarterRadius, -Math.PI / 2, 0, false);
    ctx.lineTo(centerX, centerY);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Третья четверть (x <= 0, y <= 0) - треугольник
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(mathToPixelX(-radius), centerY);
    ctx.lineTo(centerX, mathToPixelY(-radius / 2));
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Четвертая четверть (x >= 0, y <= 0) - квадрат
    const squareSize = radius * scale;
    ctx.beginPath();
    ctx.rect(centerX, centerY, squareSize, squareSize);
    ctx.fill();
    ctx.stroke();
}

function drawHistoryPoints(ctx) {
    pointsHistory.forEach(point => {
        if (!currentR || Math.abs(parseFloat(point.r) - currentR) < 0.001) {
            const x = mathToPixelX(parseFloat(point.x));
            const y = mathToPixelY(parseFloat(point.y));

            ctx.beginPath();
            ctx.arc(x, y, GRAPH_CONFIG.pointRadius, 0, Math.PI * 2);
            ctx.fillStyle = point.hit ? '#00ff00' : '#ff0000';
            ctx.fill();
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = 1;
            ctx.stroke();
        }
    });
}

function sendPointFromGraph(x, y) {
    const roundedX = Math.round(x * 100) / 100;
    const roundedY = Math.round(y * 100) / 100;

    const formData = new URLSearchParams();
    formData.append('x', roundedX.toString());
    formData.append('y', roundedY.toString());
    formData.append('r', currentR.toString());
    formData.append('source', 'graph');

    console.log('Отправка точки с графика:', {
        x: roundedX,
        y: roundedY,
        r: currentR
    });

    fetch('controller', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {

                addPointToTable(data.point);
                // Обновляем локальную историю
                pointsHistory.unshift({
                    x: parseFloat(data.point.x),
                    y: parseFloat(data.point.y),
                    r: parseFloat(data.point.r),
                    hit: data.point.hit,
                    requestTime: data.point.requestTime,
                    executionTime: data.point.executionTime
                });
                redrawGraph();
                showNotification('Точка добавлена в историю', 'success');
            } else {
                showNotification('Ошибка: ' + data.error, 'error');
            }
        })
        .catch(error => {
            console.error('Ошибка при отправке точки:', error);
            showNotification('Ошибка при проверке точки: ' + error.message, 'error');
        });
}

function addPointToTable(point) {
    const tbody = document.querySelector('#resultsBody');
    if (!tbody) return;

    const resultClass = point.hit ? 'result-hit' : 'result-miss';
    const resultText = point.hit ? 'Попадание' : 'Промах';

    // Создаём настоящую строку (которая будет вставлена в таблицу)
    const realRow = document.createElement('tr');
    realRow.innerHTML = `
        <td>${point.x}</td>
        <td>${point.y}</td>
        <td>${point.r}</td>
        <td class="${resultClass}">${resultText}</td>
        <td>${point.requestTime}</td>
        <td>${point.executionTime}с</td>
    `;

    // клон для анимации
    const flyRow = realRow.cloneNode(true);
    flyRow.classList.add('fly-row');

    document.body.appendChild(flyRow);

    // стартовая точка
    const startX = (Math.random() * window.innerWidth * 1.4) - window.innerWidth * 0.7;
    const startY = (Math.random() * window.innerHeight * 1.4) - window.innerHeight * 0.7;

    flyRow.style.setProperty('--start-x', `${startX}px`);
    flyRow.style.setProperty('--start-y', `${startY}px`);

    // Координаты конечной точки: позиция первой строки таблицы
    const targetRect = tbody.getBoundingClientRect();
    const targetX = targetRect.left + 10;
    const targetY = targetRect.top + 10;

    flyRow.style.setProperty('--end-x', `${targetX}px`);
    flyRow.style.setProperty('--end-y', `${targetY}px`);

    // Когда анимация закончится — убираем клона и вставляем строку в таблицу
    flyRow.addEventListener('animationend', () => {
        flyRow.remove();

        // Удаляем "Нет данных"
        const noResults = tbody.querySelector('.no-results');
        if (noResults) noResults.remove();

        tbody.insertBefore(realRow, tbody.firstChild);
    }, { once: true });
}



function validateX() {
    const xSelect = document.getElementById('x');
    const xError = document.getElementById('xError');

    if (!xSelect.value) {
        showError(xError, 'Выберите значение X');
        return false;
    }

    hideError(xError);
    return true;
}

function validateY() {
    const yInput = document.getElementById('y');
    const yError = document.getElementById('yError');
    const value = yInput.value.trim().replace(',', '.');

    if (value === '') {
        showError(yError, 'Y не может быть пустым');
        return false;
    }

    const numValue = parseFloat(value);
    if (isNaN(numValue)) {
        showError(yError, 'Y должен быть числом');
        return false;
    }

    if (numValue < -3 || numValue > 3) {
        showError(yError, 'Y должен быть в диапазоне от -3 до 3');
        return false;
    }

    hideError(yError);
    return true;
}

function validateR() {
    const rError = document.getElementById('rError');
    if (!currentR) {
        showError(rError, 'Выберите значение R');
        return false;
    }

    hideError(rError);
    return true;
}

function showError(errorElement, message) {
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}

function hideError(errorElement) {
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

function clearHistory() {
    fetch('controller?action=clear', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('История очищена', 'success');
                // Полностью очищаем таблицу
                const tbody = document.getElementById('resultsBody');
                tbody.innerHTML = '<tr><td colspan="6" class="no-results">Нет данных</td></tr>';
                pointsHistory = [];
                redrawGraph();
            } else {
                showNotification('Ошибка при очистке истории: ' + data.error, 'error');
            }
        })
        .catch(error => {
            showNotification('Ошибка при очистке истории', 'error');
        });
}

function showNotification(message, type) {
    // Удаляем существующие уведомления
    document.querySelectorAll('.notification').forEach(n => n.remove());

    const notif = document.createElement('div');
    notif.className = `notification ${type}`;
    notif.textContent = message;
    document.body.appendChild(notif);

    setTimeout(() => {
        notif.style.opacity = '0';
        notif.style.transform = 'translateX(100px)';
        setTimeout(() => {
            if (notif.parentNode) {
                notif.parentNode.removeChild(notif);
            }
        }, 300);
    }, 3000);
}