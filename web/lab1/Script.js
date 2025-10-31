console.log('🚀 Script loading started...');

document.addEventListener('DOMContentLoaded', function() {
    console.log('📄 DOM fully loaded and parsed');
    console.log('🎯 Initializing graph...');
    initGraph();
    console.log('✅ Graph initialized');
    
    console.log('🎯 Initializing validation...');
    initValidation();
    console.log('✅ Validation initialized');
    
    console.log('🎯 Initializing history...');
    initHistory();
    console.log('✅ History initialized');
    
    console.log('🎉 All components initialized successfully!');
});

function initGraph() {
    console.log('📊 Graph initialization started');
    
    const canvas = document.getElementById('graphCanvas');
    if (!canvas) {
        console.error('❌ Canvas element not found!');
        return;
    }
    console.log('✅ Canvas element found');
    
    const ctx = canvas.getContext('2d');
    if (!ctx) {
        console.error('❌ Could not get canvas context!');
        return;
    }
    console.log('✅ Canvas context acquired');
    
    const width = canvas.width;
    const height = canvas.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const scale = 40;
    
    console.log(`📐 Canvas dimensions: ${width}x${height}, center: (${centerX}, ${centerY}), scale: ${scale}`);
    
    let currentR = 1.0;
    console.log(`🔵 Initial R value: ${currentR}`);

    function drawCoordinatePlane() {
        console.log('🖌️ Drawing coordinate plane...');
        ctx.clearRect(0, 0, width, height);
        
        // Фон
        ctx.fillStyle = 'rgba(25, 25, 60, 0.95)';
        ctx.fillRect(0, 0, width, height);
        console.log('✅ Background drawn');
        
        // Сетка
        ctx.strokeStyle = 'rgba(100, 100, 150, 0.3)';
        ctx.lineWidth = 0.5;
        
        for (let x = centerX % scale; x < width; x += scale) {
            ctx.beginPath();
            ctx.moveTo(x, 0);
            ctx.lineTo(x, height);
            ctx.stroke();
        }
        
        for (let y = centerY % scale; y < height; y += scale) {
            ctx.beginPath();
            ctx.moveTo(0, y);
            ctx.lineTo(width, y);
            ctx.stroke();
        }
        console.log('✅ Grid drawn');
        
        // Оси
        ctx.strokeStyle = '#00f3ff';
        ctx.lineWidth = 2;
        
        // Ось X
        ctx.beginPath();
        ctx.moveTo(0, centerY);
        ctx.lineTo(width, centerY);
        ctx.stroke();
        
        // Ось Y
        ctx.beginPath();
        ctx.moveTo(centerX, 0);
        ctx.lineTo(centerX, height);
        ctx.stroke();
        console.log('✅ Axes drawn');
        
        // Стрелки
        ctx.beginPath();
        ctx.moveTo(width - 10, centerY - 5);
        ctx.lineTo(width, centerY);
        ctx.lineTo(width - 10, centerY + 5);
        ctx.stroke();
        
        ctx.beginPath();
        ctx.moveTo(centerX - 5, 10);
        ctx.lineTo(centerX, 0);
        ctx.lineTo(centerX + 5, 10);
        ctx.stroke();
        console.log('✅ Arrows drawn');
        
        // Подписи
        ctx.fillStyle = '#00f3ff';
        ctx.font = '14px Arial';
        ctx.fillText('X', width - 15, centerY - 10);
        ctx.fillText('Y', centerX + 10, 15);
        
        // Разметка
        ctx.font = '12px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        
        for (let i = -5; i <= 5; i++) {
            if (i === 0) continue;
            const x = centerX + i * scale;
            if (x >= 10 && x <= width - 10) {
                ctx.beginPath();
                ctx.moveTo(x, centerY - 5);
                ctx.lineTo(x, centerY + 5);
                ctx.stroke();
                ctx.fillText(i.toString(), x, centerY + 15);
            }
        }
        
        for (let i = -5; i <= 5; i++) {
            if (i === 0) continue;
            const y = centerY - i * scale;
            if (y >= 10 && y <= height - 10) {
                ctx.beginPath();
                ctx.moveTo(centerX - 5, y);
                ctx.lineTo(centerX + 5, y);
                ctx.stroke();
                ctx.fillText(i.toString(), centerX - 15, y);
            }
        }
        
        ctx.fillText('0', centerX - 10, centerY + 15);
        console.log('✅ Labels and markings drawn');
    }

    function drawAreas() {
        if (!currentR) {
            console.log('⚠️ No R value, skipping area drawing');
            return;
        }
        
        console.log(`🎯 Drawing areas for R = ${currentR}`);
        const r = currentR;
        ctx.fillStyle = 'rgba(0, 243, 255, 0.3)';
        ctx.strokeStyle = '#00f3ff';
        ctx.lineWidth = 2;
        
        // 1-я четверть: круг радиусом R/2
        ctx.beginPath();
        const radius = (r / 2) * scale;
        ctx.arc(centerX, centerY, radius, -Math.PI / 2, 0);
        ctx.lineTo(centerX, centerY);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
        console.log('✅ Circle area drawn (1st quadrant)');
        
        // 2-я четверть: пусто
        console.log('✅ 2nd quadrant: empty (no drawing)');
        
        // 3-я четверть: прямоугольник (-R/2, -R) - (0, 0)
        ctx.beginPath();
        const rectX = centerX - (r / 2) * scale;
        const rectWidth = (r / 2) * scale;
        const rectHeight = r * scale;
        ctx.rect(rectX, centerY, rectWidth, rectHeight);
        ctx.fill();
        ctx.stroke();
        console.log('✅ Rectangle area drawn (3rd quadrant)');
        
        // 4-я четверть: треугольник (0, -R/2) - (R/2, 0)
        ctx.beginPath();
        const triangleY = centerY + (r / 2) * scale; // Y отрицательный, поэтому прибавляем
        const triangleX = centerX + (r / 2) * scale;
        ctx.moveTo(centerX, triangleY);
        ctx.lineTo(triangleX, centerY);
        ctx.lineTo(centerX, centerY);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
        console.log('✅ Triangle area drawn (4th quadrant)');
    }

    function drawPoints(points) {
        console.log(`🎯 Drawing ${points.length} points`);
        
        points.forEach((point, index) => {
            
            const x = parseFloat(point.x);
            const y = parseFloat(point.y);
            
            const canvasX = centerX + x * scale;
            const canvasY = centerY - y * scale; // Инвертируем Y для координат canvas
            
            console.log(`📍 Point ${index + 1}: (${x}, ${y}) -> canvas: (${canvasX.toFixed(1)}, ${canvasY.toFixed(1)}), hit: ${point.hit}`);
            
            if (canvasX >= 0 && canvasX <= width && canvasY >= 0 && canvasY <= height) {
                ctx.beginPath();
                ctx.arc(canvasX, canvasY, 4, 0, 2 * Math.PI);
                
                ctx.fillStyle = point.hit ? '#00ff00' : '#ff0000';
                ctx.fill();
                
                ctx.strokeStyle = '#ffffff';
                ctx.lineWidth = 1;
                ctx.stroke();
                console.log(`✅ Point ${index + 1} drawn successfully`);
            } else {
                console.log(`⚠️ Point ${index + 1} outside canvas bounds`);
            }
        });
    }

    function drawGraph() {
        console.log('🎨 Starting graph drawing process...');
        drawCoordinatePlane();
        drawAreas();
        loadHistoryPoints();
        console.log('✅ Graph drawing completed');
    }

    
    const rSelect = document.getElementById('r');
    if (rSelect) {
        rSelect.addEventListener('change', function(e) {
            currentR = parseFloat(e.target.value);
            console.log(`🔄 R value changed to: ${currentR}`);
            drawGraph();
        });
        console.log('✅ R change listener attached');
    } else {
        console.error('❌ R select element not found!');
    }

    
    drawGraph();

    
    window.graph = {
        drawGraph,
        drawPoints,
        getCurrentR: () => currentR
    };
    
    console.log('📊 Graph module exported to window.graph');
}

function initValidation() {
    console.log('🔍 Validation initialization started');
    
    const form = document.getElementById('pointForm');
    if (!form) {
        console.error('❌ Form element not found!');
        return;
    }
    console.log('✅ Form element found');
    
    const yInput = document.getElementById('y');
    const xRadios = document.querySelectorAll('input[name="x"]');
    const rSelect = document.getElementById('r');
    
    const yError = document.getElementById('yError');
    const xError = document.getElementById('xError');
    const rError = document.getElementById('rError');

    
    if (yInput) {
        yInput.addEventListener('input', validateY);
        console.log('✅ Y input listener attached');
    }

    function validateY() {
        console.log('🔍 Validating Y input...');
        const value = yInput.value.trim().replace(',', '.');
        const numValue = parseFloat(value);
        
        console.log(`📝 Y input value: "${value}", parsed: ${numValue}`);
        
        
        if (value === '') {
            showError(yError, 'Y не может быть пустым');
            console.log('❌ Y validation failed: empty value');
            return false;
        }
        
        
        if (isNaN(numValue)) {
            showError(yError, 'Y должен быть числом');
            console.log('❌ Y validation failed: not a number');
            return false;
        }
        
        
        if (numValue < -3 || numValue > 3) {
            showError(yError, 'Y должен быть в диапазоне от -3 до 3');
            console.log(`❌ Y validation failed: value ${numValue} out of range [-3, 3]`);
            return false;
        }
        
        
        if (!/^-?\d*\.?\d+$/.test(value)) {
            showError(yError, 'Y должен быть числом (разделитель - точка или запятая)');
            console.log('❌ Y validation failed: invalid number format');
            return false;
        }
        
        hideError(yError);
        console.log('✅ Y validation passed');
        return true;
    }

    function validateX() {
        console.log('🔍 Validating X input...');
        const selectedX = document.querySelector('input[name="x"]:checked');
        
        if (!selectedX) {
            showError(xError, 'Выберите значение X');
            console.log('❌ X validation failed: no value selected');
            return false;
        }
        
        const xValue = parseFloat(selectedX.value);
        console.log(`✅ X validation passed: value = ${xValue}`);
        hideError(xError);
        return true;
    }

    function validateR() {
        console.log('🔍 Validating R input...');
        const value = rSelect.value;
        
        if (!value) {
            showError(rError, 'Выберите значение R');
            console.log('❌ R validation failed: no value selected');
            return false;
        }
        
        const rValue = parseFloat(value);
        console.log(`✅ R validation passed: value = ${rValue}`);
        hideError(rError);
        return true;
    }

    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        console.log('📤 Form submission started');
        
        const isXValid = validateX();
        const isYValid = validateY();
        const isRValid = validateR();
        
        console.log(`📊 Validation results - X: ${isXValid}, Y: ${isYValid}, R: ${isRValid}`);
        
        if (isXValid && isYValid && isRValid) {
            const formData = new FormData(form);
            const data = {
                x: formData.get('x'),
                y: formData.get('y').replace(',', '.'), // запятая в точку
                r: formData.get('r')
            };
            
            console.log(`📦 Form data prepared: x=${data.x}, y=${data.y}, r=${data.r}`);
            sendPointToServer(data.x, data.y, data.r);
        } else {
            console.log('❌ Form submission cancelled due to validation errors');
        }
    });
    console.log('✅ Form submit listener attached');

    function showError(errorElement, message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
            console.log(`❌ Error shown: ${message}`);
        }
    }

    function hideError(errorElement) {
        if (errorElement) {
            errorElement.style.display = 'none';
            console.log('✅ Error hidden');
        }
    }
    
    console.log('🔍 Validation initialization completed');
}

function initHistory() {
    console.log('📚 History initialization started');
    
    const clearBtn = document.getElementById('clearHistoryBtn');
    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            console.log('🗑️ Clearing history');
            clearHistory();
        });
        console.log('✅ Clear history listener attached');
    } else {
        console.error('❌ Clear history button not found!');
    }
    
    
    loadHistory();
    console.log('📚 History initialization completed');
}

console.log('🔧 Setting up server communication functions...');

function sendPointToServer(x, y, r) {
    console.log(`📤 Sending point to server: x=${x}, y=${y}, r=${r}`);
    showLoading(true);

    const data = new URLSearchParams();
    data.append('x', x);
    data.append('y', y); 
    data.append('r', r);

    console.log(`🌐 Making POST request to /fcgi-bin/app.jar with data:`, {
        x: x,
        y: y,
        r: r
    });

    fetch('/fcgi-bin/app.jar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: data
    })
    .then(response => {
        console.log(`📨 Server response status: ${response.status} ${response.statusText}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        console.log(`📄 Response content-type: ${contentType}`);
        
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('Expected JSON response but got: ' + contentType);
        }
        
        return response.json();
    })
    .then(data => {
        console.log('✅ Server response received:', data);
        
        
        if (data.currentResult && data.history) {
            console.log('✅ Point successfully processed by server');
            updateResultsTable(data.history);
            if (window.graph) {
                console.log('🔄 Redrawing graph with new points');
                window.graph.drawGraph();
            }
        } else if (data.history) {
            
            console.log('✅ Point processed (legacy format)');
            updateResultsTable(data.history);
            if (window.graph) {
                window.graph.drawGraph();
            }
        } else if (data.error) {
            console.error('❌ Server returned error:', data.error);
            showServerError(data.error);
        } else {
            console.error('❌ Unexpected server response format:', data);
            showServerError('Unexpected server response format');
        }
    })
    .catch(error => {
        console.error('❌ Request failed:', error);
        showServerError('Network error: ' + error.message);
    })
    .finally(() => {
        showLoading(false);
        console.log('📤 Request process completed');
    });
}

function loadHistory() {
    console.log('📥 Loading history from server...');

    console.log('🌐 Making GET history request to server...');

    fetch('/fcgi-bin/app.jar', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
        }
    })
    .then(response => {
        console.log(`📨 History response status: ${response.status} ${response.statusText}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        console.log(`📄 Response content-type: ${contentType}`);
        
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('Expected JSON response but got: ' + contentType);
        }
        
        return response.json();
    })
    .then(data => {
        console.log('✅ History data received:', data);
        
        
        if (data.history) {
            updateResultsTable(data.history);
            console.log('✅ History table updated');
        } else if (data.error) {
            console.error('❌ Server returned error:', data.error);
            showServerError(data.error || 'Failed to load history');
        } else {
            console.error('❌ Unexpected server response format for history:', data);
            showServerError('Unexpected server response format');
        }
    })
    .catch(error => {
        console.error('❌ Error loading history:', error);
        showServerError('Failed to load history: ' + error.message);
        
        updateResultsTable([]);
    });
}

function loadHistoryPoints() {
    console.log('📍 Loading history points for graph...');

    fetch('/fcgi-bin/app.jar', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
        }
    })
    .then(response => {
        console.log(`📨 History points response status: ${response.status}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('Expected JSON response but got: ' + contentType);
        }
        
        return response.json();
    })
    .then(data => {
        console.log('✅ History points data received');
        
        if (data.history) {
            const currentR = window.graph ? window.graph.getCurrentR() : 1;
            console.log(`🔍 Filtering points for current R: ${currentR}`);
            
            const pointsForCurrentR = data.history.filter(point => {
                const pointR = parseFloat(point.r);
                const currentRValue = parseFloat(currentR);
                return Math.abs(pointR - currentRValue) < 0.001; // Сравнение с плавающей точкой
            });
            
            console.log(`📊 Found ${pointsForCurrentR.length} points for R=${currentR}`);
            if (window.graph) {
                window.graph.drawPoints(pointsForCurrentR);
            }
        }
    })
    .catch(error => {
        console.error('❌ Error loading history points:', error);
        
    });
}

function clearHistory() {
    console.log('🗑️ Clearing history...');

    console.log('🌐 Making clear history request to server...');

    fetch('/fcgi-bin/app.jar?action=clear', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
        }
    })
    .then(response => {
        console.log(`📨 Clear response status: ${response.status} ${response.statusText}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('Expected JSON response but got: ' + contentType);
        }
        
        return response.json();
    })
    .then(data => {
        console.log('✅ History cleared response:', data);
        
        
        updateResultsTable([]);
        console.log('✅ Results table cleared');
        if (window.graph) {
            window.graph.drawGraph();
        }
    })
    .catch(error => {
        console.error('❌ Error clearing history:', error);
        showServerError('Failed to clear history: ' + error.message);
    });
}

function updateResultsTable(history) {
    console.log(`🔄 Updating results table with ${history.length} records`);
    
    const tbody = document.getElementById('resultsBody');
    if (!tbody) {
        console.error('❌ Results table body not found!');
        return;
    }
    
    if (history.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="no-results">Нет данных</td></tr>';
        console.log('✅ Results table updated: no data message');
        return;
    }
    
    let html = '';
    history.reverse().forEach((record, index) => {
        html += `
            <tr>
                <td>${record.x}</td>
                <td>${record.y}</td>
                <td>${record.r}</td>
                <td>${record.hit ? '✅ Попадание' : '❌ Промах'}</td>
                <td>${record.requestTime}</td>
            </tr>
        `;
    });
    
    tbody.innerHTML = html;
    console.log(`✅ Results table updated with ${history.length} records`);
}

function showLoading(show) {
    console.log(`🔄 Setting loading state: ${show}`);
    
    const submitBtn = document.querySelector('#pointForm button[type="submit"]');
    if (submitBtn) {
        if (show) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '⌛ Отправка...';
            console.log('✅ Loading state activated');
        } else {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Проверить точку';
            console.log('✅ Loading state deactivated');
        }
    } else {
        console.error('❌ Submit button not found for loading state!');
    }
}

function showServerError(message) {
    console.error('❌ Server error:', message);
    
    
    let errorDiv = document.getElementById('serverError');
    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.id = 'serverError';
        errorDiv.style.cssText = `
            background: #ff4444;
            color: white;
            padding: 10px;
            margin: 10px 0;
            border-radius: 5px;
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            max-width: 300px;
        `;
        document.body.appendChild(errorDiv);
    }
    
    errorDiv.textContent = 'Ошибка: ' + message;
    errorDiv.style.display = 'block';
    
    
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 5000); //5 сек
}

// WebGL инициализация для футера
function initWebGLFooter() {
    console.log('🎮 Initializing WebGL footer...');
    
    const canvas = document.getElementById('webgl-canvas');
    if (!canvas) {
        console.error('❌ WebGL canvas not found!');
        return;
    }

    // Получаем контекст WebGL
    const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
    if (!gl) {
        console.error('❌ WebGL not supported!');
        return;
    }

    console.log('✅ WebGL context acquired');

    // Устанавливаем размер canvas
    canvas.width = 100;
    canvas.height = 100;

    // Вершинные данные для куба
    const vertices = new Float32Array([
        // Передняя грань
        -0.5, -0.5,  0.5,
         0.5, -0.5,  0.5,
         0.5,  0.5,  0.5,
        -0.5,  0.5,  0.5,
        
        // Задняя грань
        -0.5, -0.5, -0.5,
        -0.5,  0.5, -0.5,
         0.5,  0.5, -0.5,
         0.5, -0.5, -0.5,
        
        // Верхняя грань
        -0.5,  0.5, -0.5,
        -0.5,  0.5,  0.5,
         0.5,  0.5,  0.5,
         0.5,  0.5, -0.5,
        
        // Нижняя грань
        -0.5, -0.5, -0.5,
         0.5, -0.5, -0.5,
         0.5, -0.5,  0.5,
        -0.5, -0.5,  0.5,
        
        // Правая грань
         0.5, -0.5, -0.5,
         0.5,  0.5, -0.5,
         0.5,  0.5,  0.5,
         0.5, -0.5,  0.5,
        
        // Левая грань
        -0.5, -0.5, -0.5,
        -0.5, -0.5,  0.5,
        -0.5,  0.5,  0.5,
        -0.5,  0.5, -0.5
    ]);

    // Индексы для отрисовки треугольников
    const indices = new Uint16Array([
        0, 1, 2,    0, 2, 3,    // Передняя грань
        4, 5, 6,    4, 6, 7,    // Задняя грань
        8, 9, 10,   8, 10, 11,  // Верхняя грань
        12, 13, 14, 12, 14, 15, // Нижняя грань
        16, 17, 18, 16, 18, 19, // Правая грань
        20, 21, 22, 20, 22, 23  // Левая грань
    ]);

    // Цвета для каждой грани (в формате RGB)
    const colors = new Float32Array([
        // Передняя грань - синий
        0.0, 0.5, 1.0, 0.0, 0.5, 1.0, 0.0, 0.5, 1.0, 0.0, 0.5, 1.0,
        
        // Задняя грань - фиолетовый
        0.5, 0.0, 1.0, 0.5, 0.0, 1.0, 0.5, 0.0, 1.0, 0.5, 0.0, 1.0,
        
        // Верхняя грань - голубой
        0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0,
        
        // Нижняя грань - тёмно-синий
        0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5,
        
        // Правая грань - розовый
        1.0, 0.0, 0.5, 1.0, 0.0, 0.5, 1.0, 0.0, 0.5, 1.0, 0.0, 0.5,
        
        // Левая грань - пурпурный
        0.8, 0.0, 0.8, 0.8, 0.0, 0.8, 0.8, 0.0, 0.8, 0.8, 0.0, 0.8
    ]);

    // Создаем и связываем буфер вершин
    const vertexBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, vertexBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);

    // Создаем и связываем буфер цветов
    const colorBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, colorBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, colors, gl.STATIC_DRAW);

    // Создаем и связываем буфер индексов
    const indexBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indices, gl.STATIC_DRAW);

    // Вершинный шейдер
    const vsSource = `
        attribute vec3 aPosition;
        attribute vec3 aColor;
        varying vec3 vColor;
        uniform mat4 uModelViewMatrix;
        uniform mat4 uProjectionMatrix;
        
        void main(void) {
            gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(aPosition, 1.0);
            vColor = aColor;
        }
    `;

    // Фрагментный шейдер
    const fsSource = `
        precision mediump float;
        varying vec3 vColor;
        
        void main(void) {
            gl_FragColor = vec4(vColor, 1.0);
        }
    `;

    
    const vertexShader = gl.createShader(gl.VERTEX_SHADER);
    gl.shaderSource(vertexShader, vsSource);
    gl.compileShader(vertexShader);

    const fragmentShader = gl.createShader(gl.FRAGMENT_SHADER);
    gl.shaderSource(fragmentShader, fsSource);
    gl.compileShader(fragmentShader);

    
    const shaderProgram = gl.createProgram();
    gl.attachShader(shaderProgram, vertexShader);
    gl.attachShader(shaderProgram, fragmentShader);
    gl.linkProgram(shaderProgram);
    gl.useProgram(shaderProgram);

    
    const positionAttribute = gl.getAttribLocation(shaderProgram, "aPosition");
    const colorAttribute = gl.getAttribLocation(shaderProgram, "aColor");
    const modelViewMatrixUniform = gl.getUniformLocation(shaderProgram, "uModelViewMatrix");
    const projectionMatrixUniform = gl.getUniformLocation(shaderProgram, "uProjectionMatrix");

    // Настраиваем WebGL
    gl.enable(gl.DEPTH_TEST);
    gl.clearColor(0.0, 0.0, 0.0, 0.0); // Прозрачный фон
    gl.viewport(0, 0, canvas.width, canvas.height);

    // Матрица проекции
    const projectionMatrix = new Float32Array([
        2.0, 0.0, 0.0, 0.0,
        0.0, 2.0, 0.0, 0.0,
        0.0, 0.0, -1.0, -1.0,
        0.0, 0.0, -0.2, 0.0
    ]);

    let rotation = 0;
    let animationId = null;

    function render() {
        rotation += 0.02;
        
        // Матрица модели-вида с вращением
        const modelViewMatrix = new Float32Array([
            Math.cos(rotation), 0.0, Math.sin(rotation), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -Math.sin(rotation), 0.0, Math.cos(rotation), 0.0,
            0.0, 0.0, -3.0, 1.0
        ]);

        // Очищаем canvas
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Устанавливаем матрицы
        gl.uniformMatrix4fv(projectionMatrixUniform, false, projectionMatrix);
        gl.uniformMatrix4fv(modelViewMatrixUniform, false, modelViewMatrix);

        // Вершинные атрибуты
        gl.bindBuffer(gl.ARRAY_BUFFER, vertexBuffer);
        gl.vertexAttribPointer(positionAttribute, 3, gl.FLOAT, false, 0, 0);
        gl.enableVertexAttribArray(positionAttribute);

        // Цветовые атрибуты
        gl.bindBuffer(gl.ARRAY_BUFFER, colorBuffer);
        gl.vertexAttribPointer(colorAttribute, 3, gl.FLOAT, false, 0, 0);
        gl.enableVertexAttribArray(colorAttribute);

        // Отрисовываем куб
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer);
        gl.drawElements(gl.TRIANGLES, indices.length, gl.UNSIGNED_SHORT, 0);

        animationId = requestAnimationFrame(render);
    }

    // Запускаем анимацию
    render();

    console.log('✅ WebGL cube animation started');

    // Останавливаем анимацию при скрытии страницы
    document.addEventListener('visibilitychange', function() {
        if (document.hidden) {
            if (animationId) {
                cancelAnimationFrame(animationId);
                animationId = null;
            }
        } else {
            if (!animationId) {
                render();
            }
        }
    });
}

// Инициализируем WebGL после загрузки DOM
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Script loading started...');
    console.log('📄 DOM fully loaded and parsed');
    
    console.log('🎯 Initializing graph...');
    initGraph();
    console.log('✅ Graph initialized');
    
    console.log('🎯 Initializing validation...');
    initValidation();
    console.log('✅ Validation initialized');
    
    console.log('🎯 Initializing history...');
    initHistory();
    console.log('✅ History initialized');
    
    console.log('🎮 Initializing WebGL footer...');
    initWebGLFooter();
    console.log('✅ WebGL footer initialized');
    
    console.log('🎉 All components initialized successfully!');
});
