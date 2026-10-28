/* ============================================================
   ZEEKR · Positions page
   ============================================================ */

const PAGE_SIZE = 10;

let state = { all: [], filtered: [], page: 0 };

let permissions = {
    canEdit: false,
    username: ''
};

// ---- DOM ----
const tbody        = document.getElementById('positionsBody');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const toastEl      = document.getElementById('toast');

const modal        = document.getElementById('modal');
const modalTitle   = document.getElementById('modalTitle');
const form         = document.getElementById('positionForm');
const itemIdEl     = document.getElementById('itemId');
const itemNameEl   = document.getElementById('itemName');

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

// ---- Права ----
async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        // должности могут менять ADMIN и MANAGER
        permissions.canEdit = !!(me.isAdmin);
        permissions.username = me.username || '';
    } catch (e) {
        permissions.canEdit = false;
    }
}

// ---- Загрузка ----
async function load() {
    try {
        await loadPermissions();

        const data = await PositionsApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        tbody.innerHTML = `<tr><td colspan="3" style="text-align:center; color:#ffc6d2; padding:24px;">
            ${escapeHtml(msg)}
        </td></tr>`;
    }
}

function applyFilter() {
    const kw = searchInput.value.trim().toLowerCase();
    state.filtered = kw
        ? state.all.filter(p => (p.positionName || '').toLowerCase().includes(kw))
        : state.all.slice();
    state.page = 0;
    render();
}

// ---- Отрисовка ----
function render() {
    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    const addBtn = document.getElementById('addBtn');
    if (addBtn) addBtn.style.display = permissions.canEdit ? '' : 'none';

    tbody.innerHTML = pageItems.map(p => {
        const actions = permissions.canEdit
            ? `
                <button data-action="view"   data-id="${p.id}" class="ghost">Просмотр</button>
                <button data-action="edit"   data-id="${p.id}" class="ghost">Изменить</button>
                <button data-action="delete" data-id="${p.id}" class="danger">Удалить</button>
              `
            : `
                <button data-action="view"   data-id="${p.id}" class="ghost">Просмотр</button>
              `;

        return `
            <tr>
                <td>${p.id ?? ''}</td>
                <td>${escapeHtml(p.positionName ?? '')}</td>
                <td class="actions">${actions}</td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="3" style="text-align:center;color:#7fb6dd;padding:24px">Нет данных</td></tr>`;

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

// ---- Обработчики ----
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
        const item = await PositionsApi.getById(id);
        viewContent.innerHTML = `
            <dt>ID</dt><dd>${item.id ?? ''}</dd>
            <dt>Название должности</dt><dd>${escapeHtml(item.positionName ?? '')}</dd>
        `;
        viewModal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

document.getElementById('closeViewBtn').onclick = () =>
    viewModal.classList.add('hidden');

// ---- Создание / редактирование ----
document.getElementById('addBtn').onclick = () => {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    openCreate();
};
document.getElementById('cancelBtn').onclick = () => modal.classList.add('hidden');

function openCreate() {
    modalTitle.textContent = 'Добавить должность';
    itemIdEl.value = '';
    itemNameEl.value = '';
    modal.classList.remove('hidden');
}

async function openEdit(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    try {
        const item = await PositionsApi.getById(id);
        modalTitle.textContent = 'Изменить должность';
        itemIdEl.value = item.id;
        itemNameEl.value = item.positionName ?? '';
        modal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = { positionName: itemNameEl.value.trim() };
    if (payload.positionName.length < 2) {
        showToast('Название должно содержать минимум 2 символа', 'error');
        return;
    }

    const id = itemIdEl.value;
    try {
        if (id) {
            await PositionsApi.update(Number(id), payload);
            showToast('Обновлено', 'success');
        } else {
            await PositionsApi.create(payload);
            showToast('Создано', 'success');
        }
        modal.classList.add('hidden');
        await load();
    } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
});

// ---- Удаление ----
async function removeItem(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    if (!confirm('Удалить должность #' + id + '?')) return;
    try {
        await PositionsApi.remove(id);
        showToast('Удалено', 'success');
        await load();
    } catch (e) { showToast('Ошибка удаления: ' + e.message, 'error'); }
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