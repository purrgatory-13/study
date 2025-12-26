(function () {
  const root = document.documentElement;
  const STORAGE_KEY = 'hitcheck-theme';

  const BATTERY_THEME = {
    enabled: true,
    threshold: 0.75, // 0..1  ПОРОГ ЗАРЯДА АККУМУлЯТОРА ДЛЯ СМЕНЫ ТЕМЫ
    lowTheme: 'dark'
  };

  let lastManualTheme = null;
  let batteryOverrideActive = false;
  let batteryState = null;

  function preferredTheme() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored === 'dark' || stored === 'light') return stored;
    } catch (e) {}
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  function showNotification(message) {

    if (window.PrimeFaces && typeof PrimeFaces.notify === 'function') {
      PrimeFaces.notify({
        summary: 'Внимание',
        detail: message,
        severity: 'warn',
        life: 3000
      });
      return;
    }


    const notification = document.createElement('div');
    notification.style.cssText = 'position: fixed; top: 20px; right: 20px; padding: 16px 20px; background: #f59e0b; color: white; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.3); z-index: 10000; font-family: system-ui, -apple-system, sans-serif; font-size: 14px; max-width: 320px; animation: slideIn 0.3s ease;';
    notification.innerHTML = '<strong>Внимание:</strong><br>' + message;
    document.body.appendChild(notification);
    setTimeout(() => {
      notification.style.transition = 'opacity 0.3s, transform 0.3s';
      notification.style.opacity = '0';
      notification.style.transform = 'translateX(100%)';
      setTimeout(() => notification.remove(), 300);
    }, 3000);
  }

  function updateLogoImage() {
    const logoImg = document.querySelectorAll('.logo-img');
    logoImg.forEach((img) => {
      const isDark = root.getAttribute('data-theme') === 'dark';
      const contextPath = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
      img.style.backgroundImage = `url(${contextPath}/resources/images/${isDark ? 'logo-dark' : 'logo-light'}.png)`;
    });
  }

  function updateStartButtonLogo() {

    const isDark = root.getAttribute('data-theme') === 'dark';
    const contextPath = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
    const logoUrl = `url(${contextPath}/resources/images/${isDark ? 'logo-dark' : 'logo-light'}.png)`;
    root.style.setProperty('--start-button-logo', logoUrl);
  }

  function playThemeSound() {
    try {
      const contextPath = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
      const audio = new Audio(contextPath + '/resources/sounds/theme-change.mp3');
      audio.volume = 0.3;
      audio.play().catch(() => {

      });
    } catch (e) {

    }
  }

  function applyTheme(mode, persist, skipSound) {
    const shouldPersist = (persist !== false);
    const previousTheme = root.getAttribute('data-theme');

    if (!skipSound && previousTheme && previousTheme !== mode) {
      playThemeSound();
    }
    
    root.setAttribute('data-theme', mode);
    updateLogoImage();
    updateStartButtonLogo();
    

    root.classList.add('theme-changing');
    setTimeout(() => root.classList.remove('theme-changing'), 300);
    
    if (shouldPersist) {
      lastManualTheme = mode;
      try { localStorage.setItem(STORAGE_KEY, mode); } catch (e) {}
    }
    document.querySelectorAll('.theme-toggle').forEach((btn) => {
      const icon = btn.querySelector('.theme-toggle-icon');
      if (icon) icon.textContent = (mode === 'dark') ? '🌙' : '☀️';
      btn.setAttribute('aria-label', (mode === 'dark') ? 'Включить светлую тему' : 'Включить тёмную тему');
    });
    window.dispatchEvent(new CustomEvent('themechange', { detail: { theme: mode } }));
  }

  function toggleTheme() {
    const current = root.getAttribute('data-theme') || 'light';
    const newTheme = current === 'dark' ? 'light' : 'dark';


    if (BATTERY_THEME.enabled && batteryState && newTheme === 'light') {
      const isLow = batteryState.level <= BATTERY_THEME.threshold;
      if (isLow) {
        showNotification('Использование светлой темы увеличивает расход заряда батареи.');
      }
    }
    

    applyTheme(newTheme, true, false);
    batteryOverrideActive = false;
  }

  function enforceBatteryTheme() {
    if (!BATTERY_THEME.enabled || !batteryState) return;

    const isLow = batteryState.level <= BATTERY_THEME.threshold;
    if (isLow) {
      const currentTheme = root.getAttribute('data-theme');
      if (currentTheme !== BATTERY_THEME.lowTheme) {

        if (!batteryOverrideActive && lastManualTheme !== currentTheme) {
          lastManualTheme = currentTheme;
        }
        applyTheme(BATTERY_THEME.lowTheme, false, true);
      }
      batteryOverrideActive = true;
      return;
    }


    if (batteryOverrideActive) {
      batteryOverrideActive = false;
      applyTheme(lastManualTheme || preferredTheme(), false, true); // Настроить звук при автоматической смене
    }
  }

  function initBatteryTheme() {
    if (!BATTERY_THEME.enabled || !navigator.getBattery) return;

    navigator.getBattery().then((battery) => {
      const update = () => {
        const wasLow = batteryState && batteryState.level <= BATTERY_THEME.threshold;
        batteryState = { level: battery.level, charging: battery.charging };
        const isLow = battery.level <= BATTERY_THEME.threshold;
        

        enforceBatteryTheme();
      };

      update();
      battery.addEventListener('levelchange', update);
      battery.addEventListener('chargingchange', update);
    }).catch(() => {
      batteryState = null;
    });
  }

  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.theme-toggle');
    if (!btn) return;
    e.preventDefault();
    toggleTheme();
  });

  applyTheme(preferredTheme(), true, true); // Настроить звук при инициализации
  initBatteryTheme();
  updateLogoImage();
  updateStartButtonLogo();
})();
