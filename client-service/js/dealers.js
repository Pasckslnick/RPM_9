/* ============================================================
   ZEEKR · Dealers page
   ============================================================ */

const PAGE_SIZE = 10;

let state = { all: [], filtered: [], page: 0 };

let permissions = {
    canEdit: false,
    username: ''
};

async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        permissions.canEdit = !!(me.canEditDealers || me.isAdmin || me.isManager);
        permissions.username = me.username || '';
    } catch (e) {
        permissions.canEdit = false;
    }
}

// ---- DOM ----
const tbody        = document.getElementById('dealersBody');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const toastEl      = document.getElementById('toast');

const modal        = document.getElementById('modal');
const modalTitle   = document.getElementById('modalTitle');
const form         = document.getElementById('dealerForm');
const itemIdEl     = document.getElementById('itemId');

const itemDealerNameEl = document.getElementById('itemDealerName');
const itemCityEl       = document.getElementById('itemCity');
const itemAddressEl    = document.getElementById('itemAddress');
const itemPhoneEl      = document.getElementById('itemPhone');
const itemEmailEl      = document.getElementById('itemEmail');
const itemWorkHoursEl  = document.getElementById('itemWorkHours');

const viewModal    = document.getElementById('viewModal');
const viewContent  = document.getElementById('viewContent');

// ---- Кнопка «Выйти» ----
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.onclick = () => {
        localStorage.removeItem('positions_jwt');
        window.location.href = '/login.html';
    };
}

// ---- Загрузка ----
async function load() {
    try {
        await loadPermissions();

        const data = await DealersApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center; color:#ffb3b3; padding:24px;">
            ${escapeHtml(msg)}
        </td></tr>`;
    }
}

function applyFilter() {
    const kw = searchInput.value.trim().toLowerCase();
    state.filtered = kw
        ? state.all.filter(d =>
            (d.dealerName || '').toLowerCase().includes(kw) ||
            (d.city || '').toLowerCase().includes(kw))
        : state.all.slice();
    state.page = 0;
    render();
}

// ---- Отрисовка таблицы ----
function render() {
    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    // Кнопка «+ Добавить» — только для ADMIN / MANAGER
    const addBtn = document.getElementById('addBtn');
    if (addBtn) addBtn.style.display = permissions.canEdit ? '' : 'none';

    tbody.innerHTML = pageItems.map(d => {
        const actionsHtml = permissions.canEdit
            ? `
                <button data-action="view"   data-id="${d.id}" class="ghost">Просмотр</button>
                <button data-action="edit"   data-id="${d.id}" class="ghost">Изменить</button>
                <button data-action="delete" data-id="${d.id}" class="danger">Удалить</button>
              `
            : `
                <button data-action="view"   data-id="${d.id}" class="ghost">Просмотр</button>
              `;

        return `
            <tr>
                <td>${d.id ?? ''}</td>
                <td>${escapeHtml(d.dealerName ?? '')}</td>
                <td>${escapeHtml(d.city ?? '')}</td>
                <td>${escapeHtml(d.address ?? '')}</td>
                <td>${escapeHtml(d.phone ?? '')}</td>
                <td>${escapeHtml(d.email ?? '')}</td>
                <td>${escapeHtml(d.workHours ?? '')}</td>
                <td class="actions">${actionsHtml}</td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="8" style="text-align:center;color:#7f7f7f;padding:24px">Нет данных</td></tr>`;

    renderPagination();
}

function renderPagination() {
    const total = Math.max(1, Math.ceil(state.filtered.length / PAGE_SIZE));
    let html = '';
    for (let i = 0; i < total; i++) {
        html += `<button data-page="${i}" class="ghost" ${i === state.page ? 'disabled' : ''}>${i + 1}</button>`;
    }
    paginationEl.innerHTML = html;
}

// ---- Обработчики таблицы ----
tbody.addEventListener('click', async (e) => {
    const btn = e.target.closest('button[data-action]');
    if (!btn) return;
    const id = Number(btn.dataset.id);
    const action = btn.dataset.action;

    if (action === 'view')   await openView(id);
    if (action === 'edit')   await openEdit(id);
    if (action === 'delete') await removeItem(id);
});

paginationEl.addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-page]');
    if (!btn) return;
    state.page = Number(btn.dataset.page);
    render();
});

// ---- Просмотр ----
async function openView(id) {
    try {
        const item = await DealersApi.getById(id);
        viewContent.innerHTML = `
            <dt>ID</dt><dd>${item.id ?? ''}</dd>
            <dt>Название</dt><dd>${escapeHtml(item.dealerName ?? '')}</dd>
            <dt>Город</dt><dd>${escapeHtml(item.city ?? '')}</dd>
            <dt>Адрес</dt><dd>${escapeHtml(item.address ?? '')}</dd>
            <dt>Телефон</dt><dd>${escapeHtml(item.phone ?? '')}</dd>
            <dt>Email</dt><dd>${escapeHtml(item.email ?? '')}</dd>
            <dt>Часы работы</dt><dd>${escapeHtml(item.workHours ?? '')}</dd>
        `;
        viewModal.classList.remove('hidden');
    } catch (e) {
        showToast('Ошибка: ' + e.message, 'error');
    }
}

document.getElementById('closeViewBtn').onclick = () =>
    viewModal.classList.add('hidden');

// ---- Создание / редактирование ----
document.getElementById('addBtn').onclick = () => openCreate();
document.getElementById('cancelBtn').onclick = () => modal.classList.add('hidden');

function openCreate() {
    modalTitle.textContent = 'Добавить дилера';
    itemIdEl.value = '';
    itemDealerNameEl.value = '';
    itemCityEl.value = '';
    itemAddressEl.value = '';
    itemPhoneEl.value = '';
    itemEmailEl.value = '';
    itemWorkHoursEl.value = '';
    modal.classList.remove('hidden');
}

async function openEdit(id) {
    try {
        const item = await DealersApi.getById(id);
        modalTitle.textContent = 'Изменить дилера';
        itemIdEl.value        = item.id;
        itemDealerNameEl.value = item.dealerName ?? '';
        itemCityEl.value       = item.city ?? '';
        itemAddressEl.value    = item.address ?? '';
        itemPhoneEl.value      = item.phone ?? '';
        itemEmailEl.value      = item.email ?? '';
        itemWorkHoursEl.value  = item.workHours ?? '';
        modal.classList.remove('hidden');
    } catch (e) {
        showToast('Ошибка: ' + e.message, 'error');
    }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
        dealerName: itemDealerNameEl.value.trim(),
        city:       itemCityEl.value.trim(),
        address:    itemAddressEl.value.trim(),
        phone:      itemPhoneEl.value.trim(),
        email:      itemEmailEl.value.trim(),
        workHours:  itemWorkHoursEl.value.trim()
    };

    if (!payload.dealerName || !payload.city || !payload.address
        || !payload.phone || !payload.email || !payload.workHours) {
        showToast('Заполните все поля', 'error');
        return;
    }

    const id = itemIdEl.value;
    try {
        if (id) {
            await DealersApi.update(Number(id), payload);
            showToast('Обновлено', 'success');
        } else {
            await DealersApi.create(payload);
            showToast('Создано', 'success');
        }
        modal.classList.add('hidden');
        await load();
    } catch (e) {
        showToast('Ошибка сохранения: ' + e.message, 'error');
    }
});

// ---- Удаление ----
async function removeItem(id) {
    if (!confirm('Удалить дилера #' + id + '?')) return;
    try {
        await DealersApi.remove(id);
        showToast('Удалено', 'success');
        await load();
    } catch (e) {
        showToast('Ошибка удаления: ' + e.message, 'error');
    }
}

// ---- Поиск ----
document.getElementById('searchBtn').onclick = applyFilter;
document.getElementById('clearSearchBtn').onclick = () => {
    searchInput.value = '';
    applyFilter();
};
searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') applyFilter();
});

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
    toastEl.textContent = message;
    toastEl.className = 'toast ' + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toastEl.classList.add('hidden'), 2500);
}

// ---- Старт ----
load();