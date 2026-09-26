/* ============================================================
   ZEEKR · Password field
   - Видно только последний введённый символ
   - Кнопка «показать пароль» (глаз)
   - Лицо над полем: закрывает глаза руками при вводе
   ============================================================ */

(function () {
    'use strict';

    // ---------- SVG иконки ----------
    const ICON_EYE_OPEN = `
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
             stroke-linecap="round" stroke-linejoin="round">
            <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"/>
            <circle cx="12" cy="12" r="3"/>
        </svg>`;

    const ICON_EYE_CLOSED = `
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
             stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 3l18 18"/>
            <path d="M10.6 6.2A10.9 10.9 0 0 1 12 6c6.5 0 10 7 10 7a17.7 17.7 0 0 1-3.2 4.1"/>
            <path d="M6.6 6.6A17.6 17.6 0 0 0 2 13s3.5 7 10 7a10.9 10.9 0 0 0 4.4-.9"/>
            <path d="M9.9 9.9a3 3 0 0 0 4.2 4.2"/>
        </svg>`;

    // Лицо над полем
    const FACE_OPEN = `
        <svg viewBox="0 0 120 90" xmlns="http://www.w3.org/2000/svg">
            <circle class="head" cx="60" cy="50" r="30"/>
            <circle class="eye eye-left"  cx="48" cy="46" r="3"/>
            <circle class="eye eye-right" cx="72" cy="46" r="3"/>
            <path class="mouth" d="M50 60 Q60 68 70 60"/>
            <g class="hands">
                <path class="hand hand-left"  d="M30 84 Q22 70 34 60 Q40 66 34 78 Z"/>
                <path class="hand hand-right" d="M90 84 Q98 70 86 60 Q80 66 86 78 Z"/>
            </g>
        </svg>`;

    // Тот же SVG — состояние «закрыто» управляется CSS-классом is-hidden
    const FACE_CLOSED = FACE_OPEN;

    // ---------- Инициализация поля ----------
    function initPasswordField(wrapper) {
        const input  = wrapper.querySelector('input[data-password], input[type="password"], input[type="text"]');
        const eyeBtn = wrapper.querySelector('.pw-toggle');
        const faceEl = wrapper.querySelector('.pw-face');
        if (!input) return;

        // Помечаем поле и переключаем на text + CSS-маску
        input.setAttribute('data-password', 'true');
        input.setAttribute('autocomplete', input.getAttribute('autocomplete') || 'off');
        input.setAttribute('spellcheck', 'false');
        input.setAttribute('autocapitalize', 'off');
        input.type = 'text';
        input.classList.add('pw-masked');   // CSS-маска через -webkit-text-security

        let realValue = input.value || '';
        let revealed  = false;
        let composing = false;

        // ---------- Маска ----------
        function renderMasked() {
            if (revealed) {
                input.value = realValue;
                return;
            }
            if (!realValue.length) {
                input.value = '';
                return;
            }
            const last = realValue.slice(-1);
            const dots = '•'.repeat(realValue.length - 1);
            input.value = dots + last;
        }

        // ---------- Лицо ----------
        function setFaceHidden(hidden) {
            if (!faceEl) return;
            faceEl.classList.toggle('is-hidden', !!hidden);
        }

        function updateFace() {
            if (revealed) { setFaceHidden(false); return; }
            setFaceHidden(realValue.length > 0);
        }

        // ---------- Ввод ----------
        input.addEventListener('compositionstart', () => { composing = true; });
        input.addEventListener('compositionend', () => {
            composing = false;
            renderMasked();
            updateFace();
        });

        input.addEventListener('beforeinput', (e) => {
            if (composing) return;
            const t = e.inputType;

            if (t === 'insertText' || t === 'insertFromPaste' || t === 'insertCompositionText') {
                realValue += e.data || '';
                e.preventDefault();
                renderMasked();
                updateFace();
            } else if (t === 'deleteContentBackward') {
                realValue = realValue.slice(0, -1);
                e.preventDefault();
                renderMasked();
                updateFace();
            } else if (t === 'deleteContentForward' || t === 'insertLineBreak') {
                e.preventDefault();
            }
        });

        // Каретка в конец
        input.addEventListener('click', () => {
            const len = input.value.length;
            try { input.setSelectionRange(len, len); } catch (_) {}
        });

        // ---------- Глаз ----------
        if (eyeBtn) {
            eyeBtn.innerHTML = ICON_EYE_CLOSED;

            const show = (e) => {
                if (e) e.preventDefault();
                revealed = true;
                input.classList.remove('pw-masked');
                input.value = realValue;
                eyeBtn.innerHTML = ICON_EYE_OPEN;
                eyeBtn.setAttribute('aria-label', 'Скрыть пароль');
                setFaceHidden(false);
            };
            const hide = () => {
                if (!revealed) return;
                revealed = false;
                input.classList.add('pw-masked');
                renderMasked();
                eyeBtn.innerHTML = ICON_EYE_CLOSED;
                eyeBtn.setAttribute('aria-label', 'Показать пароль');
                updateFace();
            };

            eyeBtn.addEventListener('mousedown', show);
            eyeBtn.addEventListener('mouseup', hide);
            eyeBtn.addEventListener('mouseleave', hide);
            eyeBtn.addEventListener('touchstart', show, { passive: false });
            eyeBtn.addEventListener('touchend', hide);
            eyeBtn.addEventListener('click', (e) => e.preventDefault());
        }

        // ---------- Фокус / блюр ----------
        input.addEventListener('focus', updateFace);
        input.addEventListener('blur', () => {
            if (!revealed) renderMasked();
            setFaceHidden(false);
        });

        // ---------- Перед отправкой формы ----------
        const form = input.form;
        if (form) form.addEventListener('submit', () => { input.value = realValue; });

        // ---------- Публичное поле ----------
        Object.defineProperty(input, 'realValue', {
            get() { return realValue; },
            set(v) { realValue = String(v ?? ''); renderMasked(); updateFace(); }
        });

        // Инициализация
        setFaceHidden(false);
        if (realValue) renderMasked();
        updateFace();
    }

    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('.pw-field').forEach(initPasswordField);
    });

    window.initPasswordFields = () => {
        document.querySelectorAll('.pw-field').forEach(initPasswordField);
    };
})();