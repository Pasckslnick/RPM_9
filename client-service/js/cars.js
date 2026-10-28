/* ============================================================
   ZEEKR · Cars catalog + model modal
   ============================================================ */

const PAGE_SIZE = 6;

let state = {
    all: [],
    filtered: [],
    page: 0,
    activeModel: null,
    complecttions: [],
    activeCompIndex: 0
};

let permissions = { canEdit: false, username: '' };

// ---- DOM ----
const grid         = document.getElementById('carsGrid');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const sortSelect   = document.getElementById('sortSelect');
const bodySelect   = document.getElementById('bodySelect');
const toastEl      = document.getElementById('toast');

// ---- Модалка модели ----
const modelModal      = document.getElementById('modelModal');
const modelHero       = document.getElementById('modelHero');
const modelTitle      = document.getElementById('modelTitle');
const modelDescr      = document.getElementById('modelDescr');
const modelSpecs      = document.getElementById('modelSpecs');
const compTabs        = document.getElementById('compTabs');
const compPanel       = document.getElementById('compPanel');
const testDriveBtn    = document.getElementById('testDriveBtn');
const cartBtn         = document.getElementById('cartBtn');
const editModelBtn    = document.getElementById('editModelBtn');
const deleteModelBtn  = document.getElementById('deleteModelBtn');
const addCompBtn      = document.getElementById('addCompBtn');

// ---- Форма модели ----
const modal      = document.getElementById('modal');
const modalTitle = document.getElementById('modalTitle');
const form       = document.getElementById('carForm');
const itemIdEl   = document.getElementById('itemId');
const nameEl     = document.getElementById('itemModelName');
const bodyEl     = document.getElementById('itemBodyType');
const descrEl    = document.getElementById('itemDescription');
const priceEl    = document.getElementById('itemBasePrice');
const yearEl     = document.getElementById('itemReleaseYear');
const availEl    = document.getElementById('itemAvailable');

// ---- Форма комплектации ----
const compModal      = document.getElementById('compModal');
const compModalTitle = document.getElementById('compModalTitle');
const compForm       = document.getElementById('compForm');
const compIdEl       = document.getElementById('compId');
const compCarModelIdEl = document.getElementById('compCarModelId');
const compNameEl     = document.getElementById('compName');
const compBatteryEl  = document.getElementById('compBattery');
const compPowerEl    = document.getElementById('compPower');
const compRangeEl    = document.getElementById('compRange');
const compMaxSpeedEl = document.getElementById('compMaxSpeed');
const compPriceEl    = document.getElementById('compPrice');


// ---- Права ----
async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        permissions.canEdit = !!(me.isAdmin || me.isManager);
        permissions.username = me.username || '';
    } catch (e) { permissions.canEdit = false; }
}

// ---- Загрузка списка моделей ----
async function load() {
    try {
        await loadPermissions();
        const data = await CarModelsApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        grid.innerHTML = `<p class="cars-empty" style="color:#ffb3b3;">${escapeHtml(msg)}</p>`;
    }
}

function applyFilter() {
    const kw   = searchInput.value.trim().toLowerCase();
    const body = bodySelect.value;
    const sort = sortSelect.value;

    let list = state.all.slice();
    if (kw) list = list.filter(m => (m.modelName || '').toLowerCase().includes(kw));
    if (body) list = list.filter(m => (m.bodyType || '') === body);

    switch (sort) {
        case 'price-asc':  list.sort((a,b) => (a.basePrice ?? 0) - (b.basePrice ?? 0)); break;
        case 'price-desc': list.sort((a,b) => (b.basePrice ?? 0) - (a.basePrice ?? 0)); break;
        case 'year-desc':  list.sort((a,b) => (b.releaseYear ?? 0) - (a.releaseYear ?? 0)); break;
    }

    state.filtered = list;
    state.page = 0;
    render();
}

// ---- Отрисовка сетки ----
function render() {
    const addBtn = document.getElementById('addBtn');
    if (addBtn) addBtn.style.display = permissions.canEdit ? '' : 'none';

    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    if (!pageItems.length) {
        grid.innerHTML = `<p class="cars-empty">Нет моделей</p>`;
        paginationEl.innerHTML = '';
        return;
    }

    grid.innerHTML = pageItems.map(m => {
        const img = pickImageForModel(m.modelName, m.bodyType);
        return `
            <article class="car-card" data-id="${m.id}">
                <div class="car-card__img" style="background-image:url('${img}')"></div>
                <div class="car-card__body">
                    <h3 class="car-card__title">${escapeHtml((m.modelName || '').toUpperCase())}</h3>
                    <p class="car-card__subtitle">${escapeHtml(m.description ?? '')}</p>
                    <div class="car-card__specs">
                        <span class="chip">${escapeHtml(m.bodyType || '—')}</span>
                        <span class="chip">${m.releaseYear ?? '—'} год</span>
                    </div>
                    <div class="car-card__price">от ${formatPrice(m.basePrice)} ₽</div>
                    <div class="car-card__actions">
                        <button class="primary">Подробнее →</button>
                    </div>
                </div>
            </article>
        `;
    }).join('');

    renderPagination();
}

function renderPagination() {
    const total = Math.max(1, Math.ceil(state.filtered.length / PAGE_SIZE));
    if (total <= 1) { paginationEl.innerHTML = ''; return; }
    let html = '';
    for (let i = 0; i < total; i++) {
        html += `<button data-page="${i}" class="ghost" ${i === state.page ? 'disabled' : ''}>${i + 1}</button>`;
    }
    paginationEl.innerHTML = html;
}

// ---- Клики ----
grid.addEventListener('click', (e) => {
    const card = e.target.closest('.car-card');
    if (!card) return;
    openModelModal(Number(card.dataset.id));
});

paginationEl.addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-page]');
    if (!btn) return;
    state.page = Number(btn.dataset.page);
    render();
});

// ---- Модалка модели ----
async function openModelModal(id) {
    try {
        const model = await CarModelsApi.getById(id);
        state.activeModel = model;

        // комплектации
        try {
            const comps = await apiFetch(
                `/car-service/api/car-complecttions/by-model/${id}`,
                { method: 'GET' }
            );
            state.complecttions = Array.isArray(comps) ? comps : [];
        } catch (_) { state.complecttions = []; }

        state.activeCompIndex = 0;

        // hero
        modelHero.style.backgroundImage = `url('${pickImageForModel(model.modelName, model.bodyType)}')`;
        modelTitle.textContent = (model.modelName || '').toUpperCase();
        modelDescr.textContent = model.description || '';

        // specs
        modelSpecs.innerHTML = [
            ['Кузов', model.bodyType || '—'],
            ['Год выпуска', model.releaseYear ?? '—'],
            ['Базовая цена', formatPrice(model.basePrice) + ' ₽'],
            ['Доступна', model.available ? 'Да' : 'Нет'],
            ['Комплектаций', state.complecttions.length],
        ].map(([k,v]) => `
            <div class="model-spec">
                <span class="model-spec__label">${escapeHtml(k)}</span>
                <span class="model-spec__value">${escapeHtml(String(v))}</span>
            </div>
        `).join('');

        // tabs
        renderCompTabs();

        // actions видимость
        editModelBtn.style.display = permissions.canEdit ? '' : 'none';
        deleteModelBtn.style.display = permissions.canEdit ? '' : 'none';
        addCompBtn.style.display = permissions.canEdit ? '' : 'none';

        modelModal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    } catch (e) {
        showToast('Ошибка: ' + e.message, 'error');
    }
}

function closeModelModal() {
    modelModal.classList.add('hidden');
    document.body.style.overflow = '';
}
document.getElementById('modelModalClose').onclick = closeModelModal;
modelModal.addEventListener('click', (e) => {
    if (e.target === modelModal) closeModelModal();
});
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !modelModal.classList.contains('hidden')) closeModelModal();
});

// ---- Табы комплектаций ----
function renderCompTabs() {
    if (!state.complecttions.length) {
        compTabs.innerHTML = '';
        compPanel.innerHTML = `<p style="color:#7f7f7f;">Комплектаций пока нет</p>`;
        return;
    }

    compTabs.innerHTML = state.complecttions.map((c, i) => `
        <button class="comp-tab ${i === state.activeCompIndex ? 'active' : ''}"
                data-index="${i}">
            ${escapeHtml(c.configurationName || ('Комплектация ' + (i+1)))}
        </button>
    `).join('');

    compTabs.querySelectorAll('.comp-tab').forEach(btn => {
        btn.onclick = () => {
            state.activeCompIndex = Number(btn.dataset.index);
            renderCompTabs();
        };
    });

    renderCompPanel();
}

function renderCompPanel() {
    const c = state.complecttions[state.activeCompIndex];
    if (!c) { compPanel.innerHTML = ''; return; }

    compPanel.innerHTML = `
        <div class="comp-panel__specs">
            <div class="comp-panel__spec"><span class="label">Запас хода</span><span class="value">${c.rangeKm ?? '—'} км</span></div>
            <div class="comp-panel__spec"><span class="label">Мощность</span><span class="value">${c.powerHp ?? '—'} л.с.</span></div>
            <div class="comp-panel__spec"><span class="label">Батарея</span><span class="value">${c.batteryCapacity ?? '—'} кВт·ч</span></div>
            <div class="comp-panel__spec"><span class="label">Макс. скорость</span><span class="value">${c.maxSpeed ?? '—'} км/ч</span></div>
        </div>
        <div class="comp-panel__side">
            <div class="comp-panel__price">${formatPrice(c.price)} ₽</div>
            ${permissions.canEdit ? `
                <div class="comp-panel__actions">
                    <button class="ghost" id="editCompBtn">Изменить</button>
                    <button class="danger" id="deleteCompBtn">Удалить</button>
                </div>
            ` : ''}
        </div>
    `;

    if (permissions.canEdit) {
        document.getElementById('editCompBtn').onclick = () => openEditComp(c);
        document.getElementById('deleteCompBtn').onclick = () => removeComp(c.id);
    }
}


const tdModal        = document.getElementById('tdModal');
const tdForm         = document.getElementById('tdForm');
const tdModelNameEl  = document.getElementById('tdModelName');
const tdCompEl       = document.getElementById('tdComplecttion');
const tdDealerEl     = document.getElementById('tdDealer');
const tdDateEl       = document.getElementById('tdDate');
const tdCommentEl    = document.getElementById('tdComment');

// Закрытие
document.getElementById('tdCancelBtn').onclick = () => tdModal.classList.add('hidden');
tdModal.addEventListener('click', (e) => {
    if (e.target === tdModal) tdModal.classList.add('hidden');
});
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !tdModal.classList.contains('hidden')) tdModal.classList.add('hidden');
});

// Открыть форму тест-драйва
async function openTestDriveModal() {
    const m = state.activeModel;
    if (!m) return;

    // 1. Модель — readonly
    tdModelNameEl.value = m.modelName || '';

    // 2. Комплектации модели
    const comps = state.complecttions || [];
    if (!comps.length) {
        showToast('У этой модели нет комплектаций — сначала добавьте', 'error');
        return;
    }

    tdCompEl.innerHTML = '<option value="">— выберите комплектацию —</option>' +
        comps.map(c => `<option value="${c.id}">${escapeHtml(c.configurationName ?? ('#' + c.id))}</option>`).join('');

    // Если пользователь уже выбрал комплектацию в табах — предвыберем её
    const active = comps[state.activeCompIndex];
    if (active) tdCompEl.value = String(active.id);

    // 3. Дилеры
    try {
        const dealers = await DealersApi.getAll();
        const list = Array.isArray(dealers) ? dealers : [];
        tdDealerEl.innerHTML = '<option value="">— выберите дилера —</option>' +
            list.map(d => `<option value="${d.id}">${escapeHtml(d.dealerName ?? ('#' + d.id))}</option>`).join('');
    } catch (e) {
        showToast('Не удалось загрузить дилеров: ' + e.message, 'error');
    }

    // 4. Дата — сегодня
    tdDateEl.value = new Date().toISOString().slice(0, 10);
    tdCommentEl.value = '';

    tdModal.classList.remove('hidden');
}

testDriveBtn.onclick = openTestDriveModal;

// Отправка формы
tdForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const model = state.activeModel;
    if (!model) { showToast('Модель не выбрана', 'error'); return; }

    const compId   = Number(tdCompEl.value);
    const dealerId = Number(tdDealerEl.value);
    if (!compId || !dealerId) {
        showToast('Выберите комплектацию и дилера', 'error'); return;
    }

    // 1. Найдём автора записи — пусть будет текущий пользователь.
    //    Пытаемся получить id через /api/users?keyword=<username>
// 1. Берём id текущего пользователя из /api/auth/profile
    let userId = null;
    try {
        const profile = await AuthMe.get();
        if (profile && profile.id) userId = profile.id;
    } catch (e) {
        console.error('Не удалось получить профиль:', e);
    }

    if (!userId) {
        showToast('Не удалось определить пользователя. Войдите заново.', 'error');
        return;
    }

    const payload = {
        userId,
        carModelId: model.id,
        dealerId,
        testDriveDate: tdDateEl.value,
        status:        'Новая',
        comment:       tdCommentEl.value.trim()
    };

    if (!payload.comment || payload.comment.length < 3) {
        showToast('Комментарий минимум 3 символа', 'error'); return;
    }

    try {
        await TestDrivesApi.create(payload);
        tdModal.classList.add('hidden');
        showToast('Вы записаны на тест-драйв', 'success');
    } catch (e) {
        showToast('Ошибка записи: ' + e.message, 'error');
    }
});

cartBtn.onclick = addToCart;

async function addToCart() {
    const model = state.activeModel;
    if (!model) { showToast('Модель не выбрана', 'error'); return; }

    const comps = state.complecttions || [];
    if (!comps.length) {
        showToast('У этой модели нет комплектаций', 'error');
        return;
    }

    // Выбранная комплектация — та, что активна в табах
    const comp = comps[state.activeCompIndex] || comps[0];
    if (!comp) { showToast('Комплектация не найдена', 'error'); return; }

    // 1. Узнаём текущего пользователя
    let userId = null;
    try {
        const profile = await AuthMe.get();
        if (profile && profile.id) userId = profile.id;
    } catch (e) {
        console.error('profile:', e);
    }
    if (!userId) {
        showToast('Не удалось определить пользователя. Войдите заново.', 'error');
        return;
    }

    // 2. Ищем открытую корзину у этого пользователя
    let card = null;
    try {
        const cards = await CardsApi.getAll();
        const list = Array.isArray(cards) ? cards : [];
        card = list.find(c => c.status === 'Открыта') || null;
    } catch (e) {
        console.warn('cards:', e.message);
    }

    // 3. Если нет — создаём
    if (!card) {
        try {
            card = await CardsApi.create({
                user:      { id: userId },
                createdAt: new Date().toISOString().slice(0, 10),
                status:    'Открыта'
            });
        } catch (e) {
            showToast('Не удалось создать корзину: ' + e.message, 'error');
            return;
        }
    }

    // 4. Добавляем позицию
    const payload = {
        card:         { id: card.id },
        complecttion: { id: comp.id },
        quantity:     1,
        price:        comp.price
    };

    try {
        await CardDetailsApi.create(payload);
        showToast('Добавлено в корзину', 'success');

        // опционально — переход в корзину через 600 мс
        setTimeout(() => {
            window.location.href = `/card.html?id=${card.id}`;
        }, 600);
    } catch (e) {
        showToast('Ошибка добавления: ' + e.message, 'error');
    }
}
// ---- Модалка формы модели ----
document.getElementById('addBtn').onclick = () => {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    modalTitle.textContent = 'Добавить модель';
    itemIdEl.value = '';
    nameEl.value = '';
    bodyEl.value = '';
    descrEl.value = '';
    priceEl.value = '';
    yearEl.value = new Date().getFullYear();
    availEl.checked = true;
    modal.classList.remove('hidden');
};

document.getElementById('cancelBtn').onclick = () => modal.classList.add('hidden');

editModelBtn.onclick = () => {
    const m = state.activeModel;
    if (!m) return;
    modalTitle.textContent = 'Изменить модель';
    itemIdEl.value = m.id;
    nameEl.value   = m.modelName ?? '';
    bodyEl.value   = m.bodyType ?? '';
    descrEl.value  = m.description ?? '';
    priceEl.value  = m.basePrice ?? '';
    yearEl.value   = m.releaseYear ?? '';
    availEl.checked = !!m.available;
    modal.classList.remove('hidden');
};

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        modelName:   nameEl.value.trim(),
        bodyType:    bodyEl.value.trim(),
        description: descrEl.value.trim(),
        basePrice:   Number(priceEl.value),
        releaseYear: Number(yearEl.value),
        available:   !!availEl.checked
    };
    if (!payload.modelName || !payload.bodyType || !payload.description
        || !payload.basePrice || !payload.releaseYear) {
        showToast('Заполните все поля', 'error'); return;
    }

    const id = itemIdEl.value;
    try {
        if (id) {
            await CarModelsApi.update(Number(id), payload);
            showToast('Обновлено', 'success');
        } else {
            await CarModelsApi.create(payload);
            showToast('Создано', 'success');
        }
        modal.classList.add('hidden');
        await load();
        // если модалка модели открыта — перезагрузим её данные
        if (!modelModal.classList.contains('hidden') && state.activeModel?.id) {
            openModelModal(state.activeModel.id);
        }
    } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
});

deleteModelBtn.onclick = async () => {
    const m = state.activeModel;
    if (!m) return;
    if (!confirm('Удалить модель ' + m.modelName + '?')) return;
    try {
        await CarModelsApi.remove(m.id);
        showToast('Удалено', 'success');
        closeModelModal();
        await load();
    } catch (e) { showToast('Ошибка удаления: ' + e.message, 'error'); }
};

// ---- Комплектации: добавление/редактирование ----
addCompBtn.onclick = () => openCreateComp();

function openCreateComp() {
    const m = state.activeModel;
    if (!m) return;
    compModalTitle.textContent = 'Добавить комплектацию';
    compIdEl.value = '';
    compCarModelIdEl.value = m.id;
    compNameEl.value = '';
    compBatteryEl.value = '';
    compPowerEl.value = '';
    compRangeEl.value = '';
    compMaxSpeedEl.value = '';
    compPriceEl.value = '';
    compModal.classList.remove('hidden');
}

function openEditComp(c) {
    compModalTitle.textContent = 'Изменить комплектацию';
    compIdEl.value = c.id;
    compCarModelIdEl.value = c.carModel?.id ?? state.activeModel?.id ?? '';
    compNameEl.value = c.configurationName ?? '';
    compBatteryEl.value = c.batteryCapacity ?? '';
    compPowerEl.value = c.powerHp ?? '';
    compRangeEl.value = c.rangeKm ?? '';
    compMaxSpeedEl.value = c.maxSpeed ?? '';
    compPriceEl.value = c.price ?? '';
    compModal.classList.remove('hidden');
}

document.getElementById('compCancelBtn').onclick = () => compModal.classList.add('hidden');

compForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        carModel:          { id: Number(compCarModelIdEl.value) },
        configurationName: compNameEl.value.trim(),
        batteryCapacity:   Number(compBatteryEl.value),
        powerHp:           Number(compPowerEl.value),
        rangeKm:           Number(compRangeEl.value),
        maxSpeed:          Number(compMaxSpeedEl.value),
        price:             Number(compPriceEl.value)
    };
    if (!payload.carModel.id || !payload.configurationName) {
        showToast('Заполните все поля', 'error'); return;
    }

    const id = compIdEl.value;
    try {
        if (id) {
            await CarComplectApi.update(Number(id), payload);
            showToast('Обновлено', 'success');
        } else {
            await CarComplectApi.create(payload);
            showToast('Создано', 'success');
        }
        compModal.classList.add('hidden');
        if (state.activeModel?.id) openModelModal(state.activeModel.id);
    } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
});

async function removeComp(id) {
    if (!confirm('Удалить комплектацию #' + id + '?')) return;
    try {
        await CarComplectApi.remove(id);
        showToast('Удалено', 'success');
        if (state.activeModel?.id) openModelModal(state.activeModel.id);
    } catch (e) { showToast('Ошибка удаления: ' + e.message, 'error'); }
}

// ---- Фильтры ----
searchInput.addEventListener('input', applyFilter);
sortSelect.addEventListener('change', applyFilter);
bodySelect.addEventListener('change', applyFilter);

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
load();