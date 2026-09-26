/* ============================================================
   ZEEKR · Test drives page
   ============================================================ */

const PAGE_SIZE = 10;

let state = {
    all: [],
    filtered: [],
    page: 0,
    users: [],
    carModels: [],
    dealers: []
};

let permissions = { canEdit: false, username: '' };

// ---- DOM ----
const tbody        = document.getElementById('testDrivesBody');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const toastEl      = document.getElementById('toast');

const modal        = document.getElementById('modal');
const modalTitle   = document.getElementById('modalTitle');
const form         = document.getElementById('testDriveForm');
const itemIdEl     = document.getElementById('itemId');

const itemUserEl     = document.getElementById('itemUser');
const itemCarModelEl = document.getElementById('itemCarModel');
const itemDealerEl   = document.getElementById('itemDealer');
const itemDateEl     = document.getElementById('itemDate');
const itemStatusEl   = document.getElementById('itemStatus');
const itemCommentEl  = document.getElementById('itemComment');

const viewModal    = document.getElementById('viewModal');
const viewContent  = document.getElementById('viewContent');

// ---- Logout ----
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.onclick = () => {
        localStorage.removeItem('positions_jwt');
        window.location.href = '/login.html';
    };
}

// ---- Права ----
async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        permissions.canEdit = !!(me.isAdmin || me.isManager);
        permissions.username = me.username || '';
    } catch (e) {
        permissions.canEdit = false;
    }
}

// ---- Справочники ----
async function loadDictionaries() {
    if (permissions.canEdit) {
        try {
            const users = await apiFetch('/api/users?page=0&size=100', { method: 'GET' });
            state.users = Array.isArray(users) ? users : [];
            if (itemUserEl) {
                itemUserEl.innerHTML = '<option value="">— выберите клиента —</option>' +
                    state.users.map(u =>
                        `<option value="${u.id}">${escapeHtml(u.username ?? ('#' + u.id))}</option>`
                    ).join('');
            }
        } catch (e) { console.warn('users не загрузились:', e.message); }
    }

    try {
        const models = await CarModelsApi.getAll();
        state.carModels = Array.isArray(models) ? models : [];
        if (itemCarModelEl) {
            itemCarModelEl.innerHTML = '<option value="">— выберите модель —</option>' +
                state.carModels.map(m =>
                    `<option value="${m.id}">${escapeHtml(m.modelName ?? ('#' + m.id))}</option>`
                ).join('');
        }
    } catch (e) { console.warn('car-models:', e.message); }

    try {
        const dealers = await DealersApi.getAll();
        state.dealers = Array.isArray(dealers) ? dealers : [];
        if (itemDealerEl) {
            itemDealerEl.innerHTML = '<option value="">— выберите дилера —</option>' +
                state.dealers.map(d =>
                    `<option value="${d.id}">${escapeHtml(d.dealerName ?? ('#' + d.id))}</option>`
                ).join('');
        }
    } catch (e) { console.warn('dealers:', e.message); }
}

// ---- Загрузка ----
async function load() {
    try {
        await loadPermissions();
        await loadDictionaries();

        const data = await TestDrivesApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;color:#ffb3b3;padding:24px;">
                ${escapeHtml(msg)}
            </td></tr>`;
        }
    }
}

function applyFilter() {
    if (!searchInput) return;
    const kw = searchInput.value.trim().toLowerCase();
    state.filtered = kw
        ? state.all.filter(td =>
            (td.status || '').toLowerCase().includes(kw) ||
            (td.comment || '').toLowerCase().includes(kw))
        : state.all.slice();
    state.page = 0;
    render();
}

// ---- Отрисовка ----
function render() {
    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    const addBtn  = document.getElementById('addBtn');
    const bookBtn = document.getElementById('bookBtn');

    if (addBtn)  addBtn.style.display  = permissions.canEdit ? '' : 'none';
    if (bookBtn) bookBtn.style.display = permissions.canEdit ? 'none' : '';

    if (bookBtn && !bookBtn._bound) {
        bookBtn._bound = true;
        bookBtn.onclick = () => { window.location.href = '/cars.html'; };
    }

    if (!tbody) return;

    tbody.innerHTML = pageItems.map(td => {
        const userName   = td.user && td.user.username ? td.user.username : '—';
        const modelName  = td.carModel && td.carModel.modelName ? td.carModel.modelName : '—';
        const dealerName = td.dealer && td.dealer.dealerName ? td.dealer.dealerName : '—';

        const actions = permissions.canEdit
            ? `
                <button data-action="view"   data-id="${td.id}" class="ghost">Просмотр</button>
                <button data-action="edit"   data-id="${td.id}" class="ghost">Изменить</button>
                <button data-action="delete" data-id="${td.id}" class="danger">Удалить</button>
              `
            : `
                <button data-action="view"   data-id="${td.id}" class="ghost">Просмотр</button>
              `;

        return `
            <tr>
                <td>${td.id ?? ''}</td>
                <td>${escapeHtml(userName)}</td>
                <td>${escapeHtml(modelName)}</td>
                <td>${escapeHtml(dealerName)}</td>
                <td>${escapeHtml(td.testDriveDate ?? '')}</td>
                <td>${escapeHtml(td.status ?? '')}</td>
                <td>${escapeHtml(td.comment ?? '')}</td>
                <td class="actions">${actions}</td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="8" style="text-align:center;color:#7f7f7f;padding:24px">Нет данных</td></tr>`;

    renderPagination();
}

function renderPagination() {
    if (!paginationEl) return;
    const total = Math.max(1, Math.ceil(state.filtered.length / PAGE_SIZE));
    let html = '';
    for (let i = 0; i < total; i++) {
        html += `<button data-page="${i}" class="ghost" ${i === state.page ? 'disabled' : ''}>${i + 1}</button>`;
    }
    paginationEl.innerHTML = html;
}

// ---- Обработчики таблицы ----
if (tbody) {
    tbody.addEventListener('click', async (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;
        const id = Number(btn.dataset.id);
        const action = btn.dataset.action;
        if (action === 'view')   await openView(id);
        if (action === 'edit')   await openEdit(id);
        if (action === 'delete') await removeItem(id);
    });
}

if (paginationEl) {
    paginationEl.addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-page]');
        if (!btn) return;
        state.page = Number(btn.dataset.page);
        render();
    });
}

// ---- Просмотр ----
async function openView(id) {
    try {
        const item = await TestDrivesApi.getById(id);
        const userName   = item.user && item.user.username ? item.user.username : '—';
        const modelName  = item.carModel && item.carModel.modelName ? item.carModel.modelName : '—';
        const dealerName = item.dealer && item.dealer.dealerName ? item.dealer.dealerName : '—';

        if (viewContent) {
            viewContent.innerHTML = `
                <dt>ID</dt><dd>${item.id ?? ''}</dd>
                <dt>Клиент</dt><dd>${escapeHtml(userName)}</dd>
                <dt>Модель</dt><dd>${escapeHtml(modelName)}</dd>
                <dt>Дилер</dt><dd>${escapeHtml(dealerName)}</dd>
                <dt>Дата</dt><dd>${escapeHtml(item.testDriveDate ?? '')}</dd>
                <dt>Статус</dt><dd>${escapeHtml(item.status ?? '')}</dd>
                <dt>Комментарий</dt><dd style="white-space:pre-wrap">${escapeHtml(item.comment ?? '')}</dd>
            `;
        }
        if (viewModal) viewModal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

const closeViewBtnEl = document.getElementById('closeViewBtn');
if (closeViewBtnEl) {
    closeViewBtnEl.onclick = () => viewModal && viewModal.classList.add('hidden');
}

// ---- Создание / редактирование ----
function openCreate() {
    if (modalTitle) modalTitle.textContent = 'Добавить тест-драйв';
    if (itemIdEl) itemIdEl.value = '';
    if (itemUserEl) itemUserEl.value = '';
    if (itemCarModelEl) itemCarModelEl.value = '';
    if (itemDealerEl) itemDealerEl.value = '';
    if (itemDateEl) itemDateEl.value = new Date().toISOString().slice(0, 10);
    if (itemStatusEl) itemStatusEl.value = 'Новая';
    if (itemCommentEl) itemCommentEl.value = '';
    if (modal) modal.classList.remove('hidden');
}

async function openEdit(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    try {
        const item = await TestDrivesApi.getById(id);
        if (modalTitle) modalTitle.textContent = 'Изменить тест-драйв';
        if (itemIdEl) itemIdEl.value = item.id;
        if (itemUserEl) itemUserEl.value = item.user && item.user.id ? String(item.user.id) : '';
        if (itemCarModelEl) itemCarModelEl.value = item.carModel && item.carModel.id ? String(item.carModel.id) : '';
        if (itemDealerEl) itemDealerEl.value = item.dealer && item.dealer.id ? String(item.dealer.id) : '';
        if (itemDateEl) itemDateEl.value = item.testDriveDate ?? '';
        if (itemStatusEl) itemStatusEl.value = item.status ?? '';
        if (itemCommentEl) itemCommentEl.value = item.comment ?? '';
        if (modal) modal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

const addBtnEl = document.getElementById('addBtn');
if (addBtnEl) {
    addBtnEl.onclick = () => {
        if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
        openCreate();
    };
}

const cancelBtnEl = document.getElementById('cancelBtn');
if (cancelBtnEl) {
    cancelBtnEl.onclick = () => modal && modal.classList.add('hidden');
}

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId   = Number(itemUserEl.value);
        const modelId  = Number(itemCarModelEl.value);
        const dealerId = Number(itemDealerEl.value);

        if (!userId || !modelId || !dealerId) {
            showToast('Выберите клиента, модель и дилера', 'error');
            return;
        }

        const payload = {
            user:          { id: userId },
            carModel:      { id: modelId },
            dealer:        { id: dealerId },
            testDriveDate: itemDateEl.value,
            status:        itemStatusEl.value.trim(),
            comment:       itemCommentEl.value.trim()
        };

        if (!payload.status || payload.status.length < 2) {
            showToast('Статус минимум 2 символа', 'error'); return;
        }
        if (!payload.comment || payload.comment.length < 3) {
            showToast('Комментарий минимум 3 символа', 'error'); return;
        }

        const id = itemIdEl.value;
        try {
            if (id) {
                await TestDrivesApi.update(Number(id), payload);
                showToast('Обновлено', 'success');
            } else {
                await TestDrivesApi.create(payload);
                showToast('Создано', 'success');
            }
            if (modal) modal.classList.add('hidden');
            await load();
        } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
    });
}

// ---- Удаление ----
async function removeItem(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    if (!confirm('Удалить тест-драйв #' + id + '?')) return;
    try {
        await TestDrivesApi.remove(id);
        showToast('Удалено', 'success');
        await load();
    } catch (e) { showToast('Ошибка удаления: ' + e.message, 'error'); }
}

// ---- Поиск ----
const searchBtnEl = document.getElementById('searchBtn');
if (searchBtnEl) searchBtnEl.onclick = applyFilter;

const clearSearchBtnEl = document.getElementById('clearSearchBtn');
if (clearSearchBtnEl) {
    clearSearchBtnEl.onclick = () => {
        if (searchInput) searchInput.value = '';
        applyFilter();
    };
}

if (searchInput) {
    searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') applyFilter();
    });
}

// ---- Утилиты ----
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