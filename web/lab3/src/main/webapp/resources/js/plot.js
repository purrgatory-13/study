(function (global) {
    const padding = 40;
    // Статичная сетка: диапазон от -5 до 5 по обеим осям
    const GRID_MIN = -5;
    const GRID_MAX = 5;
    const GRID_SPAN = GRID_MAX - GRID_MIN; // 10
    
    let canvas;
    let ctx;
    let remoteCommand;
    let remoteCommandName;
    let currentR = 1;
    let points = [];
    let themeListenerAdded = false;

    function readTheme() {
        const cs = getComputedStyle(document.documentElement);
        const pick = (name, fallback) => (cs.getPropertyValue(name) || '').trim() || fallback;
        return {
            axis: pick('--plot-axis', '#334155'),
            areaFill: pick('--plot-area-fill', 'rgba(59,130,246,0.35)'),
            areaStroke: pick('--plot-area-stroke', '#3b82f6'),
            hit: pick('--plot-hit', '#16a34a'),
            miss: pick('--plot-miss', '#ef4444'),
            pointStroke: pick('--plot-point-stroke', '#0f172a'),
            fontFamily: pick('--font-body', 'system-ui')
        };
    }

    // Масштаб для статичной сетки (фиксированный)
    // Используем минимальное из width/2 и height/2 для правильного отображения на обеих осях
    function gridScale() {
        const maxDimension = Math.min(canvas.width / 2 - padding, canvas.height / 2 - padding);
        return maxDimension / (GRID_SPAN / 2);
    }

    // Масштаб для области (зависит от текущего R)
    function areaScale() {
        return gridScale(); // Используем тот же масштаб, что и сетка
    }

    function center() {
        return { x: canvas.width / 2, y: canvas.height / 2 };
    }

    function toCanvas(x, y) {
        const k = gridScale();
        const c = center();
        return { x: c.x + x * k, y: c.y - y * k };
    }

    function fromCanvas(evt) {
        const rect = canvas.getBoundingClientRect();
        // Учитываем масштабирование canvas (если CSS изменяет размер)
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;
        
        // Преобразуем координаты клика в координаты canvas
        const clickX = (evt.clientX - rect.left) * scaleX;
        const clickY = (evt.clientY - rect.top) * scaleY;
        
        // Преобразуем координаты canvas в координаты графика
        const k = gridScale();
        const c = center();
        const x = (clickX - c.x) / k;
        const y = (c.y - clickY) / k;
        return { x, y };
    }

    function clear() { ctx.clearRect(0, 0, canvas.width, canvas.height); }

    function drawAxes() {
        const t = readTheme();
        const c = center();
        const k = gridScale();

        ctx.strokeStyle = t.axis;
        ctx.lineWidth = 1.5;

        // Оси координат (более толстые)
        ctx.beginPath();
        ctx.moveTo(0, c.y); ctx.lineTo(canvas.width, c.y);
        ctx.moveTo(c.x, canvas.height); ctx.lineTo(c.x, 0);
        ctx.stroke();
        
        // Восстанавливаем толщину для линий сетки
        ctx.lineWidth = 0.5;

        // Статичная сетка с шагом 1 от -5 до 5
        const ticks = [];
        for (let i = GRID_MIN; i <= GRID_MAX; i++) {
            ticks.push(i);
        }

        ctx.fillStyle = t.axis;
        ctx.font = '12px ' + t.fontFamily;

        // Рисуем деления и подписи
        ticks.forEach((tick) => {
            const px = c.x + tick * k;
            const py = c.y - tick * k;

            // Деления на осях - линии на всю ширину графика
            ctx.beginPath();
            ctx.moveTo(px, 0); ctx.lineTo(px, canvas.height);
            ctx.moveTo(0, py); ctx.lineTo(canvas.width, py);
            ctx.stroke();

            // Подписи значений
            if (tick !== 0) {
                ctx.fillText(tick.toString(), px - 6, c.y + 14);
                ctx.fillText(tick.toString(), c.x + 6, py + 4);
            }
        });

        // Подписи осей
        ctx.fillText('x', canvas.width - padding / 2, c.y - 6);
        ctx.fillText('y', c.x + 6, padding / 2);
    }

    function drawArea() {
        const t = readTheme();
        const k = areaScale(); // Используем масштаб для области
        const c = center();

        ctx.fillStyle = t.areaFill;
        ctx.strokeStyle = t.areaStroke;
        ctx.lineWidth = 1;

        // Первая четверть: четверть окружности радиусом R/2
        ctx.beginPath();
        ctx.moveTo(c.x, c.y);
        ctx.arc(c.x, c.y, k * currentR / 2, 1.5 * Math.PI, 2 * Math.PI);
        ctx.lineTo(c.x, c.y);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // Третья четверть: прямоугольник от (0,0) до (-R/2, -R)
        ctx.beginPath();
        ctx.moveTo(c.x, c.y);
        ctx.lineTo(c.x - k * currentR / 2, c.y);
        ctx.lineTo(c.x - k * currentR / 2, c.y + k * currentR);
        ctx.lineTo(c.x, c.y + k * currentR);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // Четвертая четверть: треугольник (0,0), (R,0), (0,-R)
        ctx.beginPath();
        ctx.moveTo(c.x, c.y);
        ctx.lineTo(c.x + k * currentR, c.y);
        ctx.lineTo(c.x, c.y + k * currentR);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
    }

    function drawPoints() {
        const t = readTheme();
        const radius = 4;

        points.forEach((p) => {
            const cv = toCanvas(p.x, p.y);
            ctx.beginPath();
            ctx.arc(cv.x, cv.y, radius, 0, Math.PI * 2);
            ctx.fillStyle = p.hit ? t.hit : t.miss;
            ctx.fill();
            ctx.strokeStyle = t.pointStroke;
            ctx.stroke();
        });
    }

    function drawAll() {
        if (!canvas || !ctx) { return; }
        clear();
        drawAxes(); // Сначала рисуем статичную сетку
        drawArea(); // Затем динамичную область
        drawPoints(); // И точки поверх всего
    }

    function onClick(evt) {
        const point = fromCanvas(evt);
        if (typeof remoteCommand !== 'function' && remoteCommandName) {
            remoteCommand = global[remoteCommandName];
        }
        if (typeof remoteCommand === 'function') {
            remoteCommand([
                { name: 'clickX', value: point.x.toFixed(6) },
                { name: 'clickY', value: point.y.toFixed(6) }
            ]);
        }
    }

    const api = {
        init: function (canvasId, remoteFnName, r, initialPoints) {
            canvas = document.getElementById(canvasId);
            if (!canvas) { return; }

            ctx = canvas.getContext('2d');
            remoteCommandName = remoteFnName;
            remoteCommand = global[remoteFnName];
            currentR = Number(r) || 1;
            points = Array.isArray(initialPoints) ? initialPoints : [];

            canvas.removeEventListener('click', onClick);
            canvas.addEventListener('click', onClick);

            if (!themeListenerAdded) {
                window.addEventListener('themechange', drawAll);
                themeListenerAdded = true;
            }

            drawAll();
        },
        redraw: function (r, newPoints) {
            currentR = Number(r) || currentR;
            points = Array.isArray(newPoints) ? newPoints : points;
            drawAll();
        }
    };

    global.AreaPlot = api;
})(window);
