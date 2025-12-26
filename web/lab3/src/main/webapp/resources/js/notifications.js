// Система уведомлений с выталкиванием сверху вниз
(function() {
    'use strict';
    
    let notificationContainer = null;
    let notifications = [];
    
    function initContainer() {
        if (!notificationContainer) {
            notificationContainer = document.createElement('div');
            notificationContainer.id = 'notifications-container';
            notificationContainer.style.cssText = 
                'position: fixed; top: 20px; right: 20px; z-index: 99999; ' +
                'display: flex; flex-direction: column; gap: 12px; ' +
                'max-width: 400px; pointer-events: none;';
            document.body.appendChild(notificationContainer);
        }
        return notificationContainer;
    }
    
    function getSeverityClass(severity) {
        switch(severity) {
            case 'error': return 'notification-error';
            case 'warn': case 'warning': return 'notification-warn';
            case 'info': return 'notification-info';
            case 'success': return 'notification-success';
            default: return 'notification-info';
        }
    }
    
    function getSeverityIcon(severity) {
        switch(severity) {
            case 'error': return '⚠️';
            case 'warn': case 'warning': return '⚠️';
            case 'info': return 'ℹ️';
            case 'success': return '✓';
            default: return 'ℹ️';
        }
    }
    
    window.showNotify = function(summary, detail, severity, life) {
        severity = severity || 'info';
        life = life || 4000;
        
        const container = initContainer();
        const notification = document.createElement('div');
        notification.className = 'notification ' + getSeverityClass(severity);
        
        const fullMessage = summary && detail ? summary + ': ' + detail : (summary || detail);
        const icon = getSeverityIcon(severity);
        
        notification.innerHTML = 
            '<div class="notification-content">' +
            '<span class="notification-icon">' + icon + '</span>' +
            '<span class="notification-text">' + fullMessage + '</span>' +
            '</div>';
        
        notification.style.cssText = 
            'background: var(--notify-bg, #fff); ' +
            'color: var(--notify-color, #333); ' +
            'padding: 14px 18px; ' +
            'border-radius: 8px; ' +
            'box-shadow: 0 4px 12px rgba(0,0,0,0.15); ' +
            'pointer-events: auto; ' +
            'opacity: 0; ' +
            'transform: translateX(120%); ' +
            'transition: opacity 0.3s ease, transform 0.3s ease; ' +
            'display: flex; ' +
            'align-items: center; ' +
            'min-width: 280px; ' +
            'max-width: 400px; ' +
            'font-family: var(--font-body, system-ui, sans-serif); ' +
            'font-size: 14px; ' +
            'border-left: 4px solid var(--notify-border, #6366f1);';
        
        // Установка цвета в зависимости от severity
        if (severity === 'error') {
            notification.style.borderLeftColor = '#ef4444';
            notification.style.background = 'rgba(239, 68, 68, 0.95)';
            notification.style.color = '#fff';
        } else if (severity === 'warn' || severity === 'warning') {
            notification.style.borderLeftColor = '#f59e0b';
            notification.style.background = 'rgba(245, 158, 11, 0.95)';
            notification.style.color = '#fff';
        } else if (severity === 'success') {
            notification.style.borderLeftColor = '#22c55e';
            notification.style.background = 'rgba(34, 197, 94, 0.95)';
            notification.style.color = '#fff';
        } else {
            notification.style.borderLeftColor = '#6366f1';
            notification.style.background = 'rgba(255, 255, 255, 0.95)';
            notification.style.color = '#0b1220';
        }
        
        // Тёмная тема
        if (document.documentElement.getAttribute('data-theme') === 'dark') {
            if (severity === 'info') {
                notification.style.background = 'rgba(15, 23, 42, 0.95)';
                notification.style.color = '#eef2ff';
            }
        }
        
        // Вставляем новое уведомление в начало (сверху)
        if (container.firstChild) {
            container.insertBefore(notification, container.firstChild);
        } else {
            container.appendChild(notification);
        }
        notifications.unshift(notification);
        
        // Анимация появления
        setTimeout(() => {
            notification.style.opacity = '1';
            notification.style.transform = 'translateX(0)';
        }, 10);
        
        // Автоматическое удаление
        const timeoutId = setTimeout(() => {
            removeNotification(notification);
        }, life);
        
        // Удаление при клике
        notification.addEventListener('click', () => {
            clearTimeout(timeoutId);
            removeNotification(notification);
        });
    };
    
    function removeNotification(notification) {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(120%)';
        
        setTimeout(() => {
            const index = notifications.indexOf(notification);
            if (index > -1) {
                notifications.splice(index, 1);
            }
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }
})();

