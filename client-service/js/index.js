/* ============================================================
   ZEEKR · Home page
   ============================================================ */

const toastEl = document.getElementById('toast');

// ---- Logout ----
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.onclick = () => {
        localStorage.removeItem('positions_jwt');
        window.location.href = '/login.html';
    };
}

// ---- Загрузка моделей ----
async function loadModels() {
    const wrap = document.getElementById('homeModels');
    if (!wrap) return;

    try {
        const data = await CarModelsApi.getAll();
        const models = Array.isArray(data) ? data : [];

        if (!models.length) {
            wrap.innerHTML = `<p class="cars-empty">Нет моделей</p>`;
            return;
        }

        wrap.innerHTML = models.slice(0, 6).map(m => {
            const img = pickImageForModel(m.modelName, m.bodyType);
            return `
                <a class="home-model" href="/cars.html">
                    <div class="home-model__img" style="background-image:url('${img}')"></div>
                    <div class="home-model__body">
                        <div class="home-model__name">${escapeHtml(m.modelName || 'ZEEKR')}</div>
                        <div class="home-model__sub">${escapeHtml(m.description || '')}</div>
                        <div class="home-model__row">
                            <span class="home-chip">${escapeHtml(m.bodyType || '—')}</span>
                            <span class="home-chip">${m.releaseYear ?? '—'} год</span>
                        </div>
                        <div class="home-model__more">Подробнее →</div>
                    </div>
                </a>
            `;
        }).join('');

        // Табы конфигуратора
        const tabs = document.getElementById('configModels');
        const select = document.getElementById('contactModel');
        if (tabs) {
            tabs.innerHTML = models.slice(0, 4).map((m, i) => `
                <button type="button" class="home-config__tab ${i === 0 ? 'active' : ''}"
                        data-price="${m.basePrice ?? 0}" data-name="${escapeHtml(m.modelName || '')}">
                    ${escapeHtml(m.modelName || ('#' + m.id))}
                </button>
            `).join('');

            tabs.querySelectorAll('.home-config__tab').forEach(t => {
                t.onclick = () => {
                    tabs.querySelectorAll('.home-config__tab').forEach(x => x.classList.remove('active'));
                    t.classList.add('active');
                    updateConfigPrice();
                };
            });
            updateConfigPrice();
        }

        if (select) {
            select.innerHTML = '<option value="">— выберите модель —</option>' +
                models.map(m => `<option value="${m.id}">${escapeHtml(m.modelName || '')}</option>`).join('');
        }
    } catch (e) {
        wrap.innerHTML = `<p class="cars-empty" style="color:#ffb3b3;">${escapeHtml(e.message)}</p>`;
    }
}

// ---- Цена в конфигураторе ----
function updateConfigPrice() {
    const priceEl = document.getElementById('configPrice');
    const noteEl  = document.getElementById('configPriceNote');
    if (!priceEl) return;

    const active = document.querySelector('.home-config__tab.active');
    const price  = active ? Number(active.dataset.price || 0) : 0;
    const name   = active ? active.dataset.name : '';

    priceEl.textContent = 'от ' + formatPrice(price) + ' €';
    if (noteEl) noteEl.textContent = 'для ' + (name || 'ZEEKR');
}

// ---- Цвета ----
document.querySelectorAll('.home-config__color').forEach(el => {
    el.onclick = () => {
        document.querySelectorAll('.home-config__color').forEach(x => x.classList.remove('active'));
        el.classList.add('active');
        const nameEl = document.getElementById('configColorName');
        if (nameEl) nameEl.textContent = el.dataset.color || '';
    };
});

// ---- Новости ----
async function loadNews() {
    const wrap = document.getElementById('homeNews');
    if (!wrap) return;

    try {
        const data = await NewsApi.getAll();
        const list = Array.isArray(data) ? data : [];

        if (!list.length) {
            wrap.innerHTML = `<p class="cars-empty">Нет новостей</p>`;
            return;
        }

        const main = list[0];
        const rest = list.slice(1, 4);

        const mainImg = main.image ? (main.image.startsWith('/') ? main.image : '/images/' + main.image) : '/images/001.jpg';
        const mainAuthor = main.author && main.author.username ? main.author.username : '';

        wrap.innerHTML = `
            <article class="home-news__main" style="background-image:url('${mainImg}')">
                <div class="home-news__main-inner">
                    <div class="home-eyebrow" style="color:#c9c9c9;margin-bottom:8px;">
                        ${escapeHtml(main.publishDate || '')}${mainAuthor ? ' · ' + escapeHtml(mainAuthor) : ''}
                    </div>
                    <h3>${escapeHtml(main.title || '')}</h3>
                    <p>${escapeHtml((main.content || '').slice(0, 180))}</p>
                    <a class="more" href="/news.html">Читать далее →</a>
                </div>
            </article>

            <div class="home-news__side">
                ${rest.map(n => `
                    <div class="home-news__item" onclick="window.location.href='/news.html'">
                        <div class="date">${escapeHtml(n.publishDate || '')}</div>
                        <h4>${escapeHtml(n.title || '')}</h4>
                        <p>${escapeHtml((n.content || '').slice(0, 120))}</p>
                    </div>
                `).join('')}
            </div>
        `;
    } catch (e) {
        wrap.innerHTML = `<p class="cars-empty" style="color:#ffb3b3;">${escapeHtml(e.message)}</p>`;
    }
}

// ---- Контакт: города из дилеров ----
async function loadCities() {
    const citySelect = document.getElementById('contactCity');
    const info = document.getElementById('contactInfo');
    if (!citySelect) return;

    try {
        const data = await DealersApi.getAll();
        const dealers = Array.isArray(data) ? data : [];

        const cities = [...new Set(dealers.map(d => d.city).filter(Boolean))];

        citySelect.innerHTML = '<option value="">— выберите город —</option>' +
            cities.map(c => `<option value="${escapeHtml(c)}">${escapeHtml(c)}</option>`).join('');

        // Показ первого дилера как базовой информации
        if (dealers.length && info) {
            const d = dealers[0];
            info.innerHTML = `
                <h4>${escapeHtml(d.city || 'Город')}</h4>
                <p style="color:#a0a0a0;font-size:14px;line-height:1.6;margin:0 0 16px;">
                    ${escapeHtml(d.dealerName || '')}<br>${escapeHtml(d.address || '')}
                </p>
                <a href="tel:${escapeHtml(d.phone || '')}">${escapeHtml(d.phone || '')}</a>
                <a href="mailto:${escapeHtml(d.email || '')}">${escapeHtml(d.email || '')}</a>
                <div style="color:#7a7a7a;font-size:12px;letter-spacing:1.5px;margin-top:12px;">
                    ${escapeHtml(d.workHours || '')}
                </div>
            `;
        }

        // При смене города — обновляем карточку
        citySelect.onchange = () => {
            const city = citySelect.value;
            const d = dealers.find(x => x.city === city) || dealers[0];
            if (!d || !info) return;
            info.innerHTML = `
                <h4>${escapeHtml(d.city || '')}</h4>
                <p style="color:#a0a0a0;font-size:14px;line-height:1.6;margin:0 0 16px;">
                    ${escapeHtml(d.dealerName || '')}<br>${escapeHtml(d.address || '')}
                </p>
                <a href="tel:${escapeHtml(d.phone || '')}">${escapeHtml(d.phone || '')}</a>
                <a href="mailto:${escapeHtml(d.email || '')}">${escapeHtml(d.email || '')}</a>
                <div style="color:#7a7a7a;font-size:12px;letter-spacing:1.5px;margin-top:12px;">
                    ${escapeHtml(d.workHours || '')}
                </div>
            `;
        };
    } catch (e) {
        console.warn('dealers:', e.message);
    }
}

// ---- Кнопка "Позвоните мне" ----
const contactSubmit = document.getElementById('contactSubmit');
if (contactSubmit) {
    contactSubmit.onclick = () => {
        showToast('Заявка отправлена — менеджер свяжется с вами', 'success');
    };
}

// ---- Утилиты ----
function formatPrice(v) {
    if (v == null) return '—';
    return Number(v).toLocaleString('ru-RU');
}
function pickImageForModel(name, bodyType) {
    const n = (name || '').toLowerCase();
    if (n.includes('9x'))  return '/images/9X.jpg';
    if (n.includes('7x'))  return '/images/7X.jpg';
    if (n.includes('mix')) return '/images/zeekr_mix.jpg';
    if (n.includes('001')) return '/images/001.jpg';
    if (n.includes('007')) return '/images/007.jpg';
    if (n.includes('009')) return '/images/009.jpg';
    if (n.includes('zeekr x')) return '/images/x.jpg';
    return '/images/001.jpg';
}
function escapeHtml(s) {
    return String(s)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
let toastTimer;
function showToast(message, type = '') {
    if (!toastEl) { console.warn(message); return; }
    toastEl.textContent = message;
    toastEl.className = 'toast ' + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toastEl.classList.add('hidden'), 2500);
}

// ---- Старт ----
loadModels();
loadNews();
loadCities();