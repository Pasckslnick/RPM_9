/* ============================================================
   ZEEKR · Users page (без создания)
   ============================================================ */

const PAGE_SIZE = 10;

let state = {
    all: [],
    filtered: [],
    page: 0,
    roles: [],
    positions: []
};

let permissions = { canEdit: false, username: '' };

// ---- DOM ----
const tbody        = document.getElementById('usersBody');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const toastEl      = document.getElementById('toast');

const modal        = document.getElementById('modal');
const modalTitle   = document.getElementById('modalTitle');
const form         = document.getElementById('userForm');
const itemIdEl     = document.getElementById('itemId');

const itemUsernameEl = document.getElementById('itemUsername');
const itemEmailEl    = document.getElementById('itemEmail');
const itemPhoneEl    = document.getElementById('itemPhone');
const itemRoleEl     = document.getElementById('itemRole');
const itemPositionEl = document.getElementById('itemPosition');

// Поле пароля — если в HTML его нет, всё равно работает
const itemPasswordEl = document.getElementById('itemPassword');

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

// ---- Permissions ----
async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        permissions.canEdit = !!(me.isAdmin);
        permissions.username = me.username || '';
    } catch (e) {
        permissions.canEdit = false;
    }
}

// ---- Справочники ----
async function loadDictionaries() {
    try {
        const roles = await UserRolesApi.getAll();
        state.roles = Array.isArray(roles) ? roles : [];
        itemRoleEl.innerHTML = '<option value="">— выберите роль —</option>' +
            state.roles.map(r =>
                `<option value="${r.id}">${escapeHtml(r.roleName ?? ('#' + r.id))}</option>`
            ).join('');

        const positions = await PositionsApi.getAll();
        state.positions = Array.isArray(positions) ? positions : [];
        itemPositionEl.innerHTML = '<option value="">— выберите должность —</option>' +
            state.positions.map(p =>
                `<option value="${p.id}">${escapeHtml(p.positionName ?? ('#' + p.id))}</option>`
            ).join('');
    } catch (e) {
        showToast('Ошибка загрузки справочников: ' + e.message, 'error');
    }
}

// ---- Загрузка ----
async function load() {
    try {
        await loadPermissions();
        await loadDictionaries();

        const data = await apiFetch('/api/users?page=0&size=100', { method: 'GET' });
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#ffb3b3;padding:24px;">
            ${escapeHtml(msg)}
        </td></tr>`;
    }
}

function applyFilter() {
    const kw = searchInput.value.trim().toLowerCase();
    state.filtered = kw
        ? state.all.filter(u =>
            (u.username || '').toLowerCase().includes(kw) ||
            (u.email || '').toLowerCase().includes(kw))
        : state.all.slice();
    state.page = 0;
    render();
}

// ---- Отрисовка ----
function render() {
    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    tbody.innerHTML = pageItems.map(u => {
        const roleName = state.roles.find(r => r.id === u.roleId)?.roleName ?? ('#' + (u.roleId ?? ''));
        const positionName = u.position && u.position.positionName ? u.position.positionName : '—';

        const actions = permissions.canEdit
            ? `
                <button data-action="view"   data-id="${u.id}" class="ghost">Просмотр</button>
                <button data-action="edit"   data-id="${u.id}" class="ghost">Изменить</button>
                <button data-action="delete" data-id="${u.id}" class="danger">Удалить</button>
              `
            : `
                <button data-action="view"   data-id="${u.id}" class="ghost">Просмотр</button>
              `;

        return `
            <tr>
                <td>${u.id ?? ''}</td>
                <td>${escapeHtml(u.username ?? '')}</td>
                <td>${escapeHtml(u.email ?? '')}</td>
                <td>${escapeHtml(u.phone ?? '')}</td>
                <td>${escapeHtml(roleName)}</td>
                <td>${escapeHtml(positionName)}</td>
                <td class="actions">${actions}</td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="7" style="text-align:center;color:#7f7f7f;padding:24px">Нет данных</td></tr>`;

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

// ---- Клики по таблице ----
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
        const u = await apiFetch(`/api/users/${id}`, { method: 'GET' });
        const roleName = state.roles.find(r => r.id === u.roleId)?.roleName ?? ('#' + (u.roleId ?? ''));
        const positionName = u.position && u.position.positionName ? u.position.positionName : '—';

        viewContent.innerHTML = `
            <dt>ID</dt><dd>${u.id ?? ''}</dd>
            <dt>Логин</dt><dd>${escapeHtml(u.username ?? '')}</dd>
            <dt>Email</dt><dd>${escapeHtml(u.email ?? '')}</dd>
            <dt>Телефон</dt><dd>${escapeHtml(u.phone ?? '')}</dd>
            <dt>Роль</dt><dd>${escapeHtml(roleName)}</dd>
            <dt>Должность</dt><dd>${escapeHtml(positionName)}</dd>
        `;
        viewModal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

document.getElementById('closeViewBtn').onclick = () =>
    viewModal.classList.add('hidden');

// ---- Редактирование ----
document.getElementById('cancelBtn').onclick = () => modal.classList.add('hidden');

function clearPasswordField() {
    if (!itemPasswordEl) return;
    if ('realValue' in itemPasswordEl) itemPasswordEl.realValue = '';
    else itemPasswordEl.value = '';
}

async function openEdit(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    try {
        const u = await apiFetch(`/api/users/${id}`, { method: 'GET' });
        if (modalTitle) modalTitle.textContent = 'Изменить пользователя';
        itemIdEl.value        = u.id;
        itemUsernameEl.value  = u.username ?? '';
        itemEmailEl.value     = u.email ?? '';
        itemPhoneEl.value     = u.phone ?? '';
        itemRoleEl.value      = u.roleId ?? '';
        itemPositionEl.value  = u.position && u.position.id ? String(u.position.id) : '';
        clearPasswordField();
        modal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
        username: itemUsernameEl.value.trim(),
        email:    itemEmailEl.value.trim(),
        phone:    itemPhoneEl.value.trim(),
        roleId:   Number(itemRoleEl.value),
        position: { id: Number(itemPositionEl.value) }
    };

    if (!payload.username || !payload.email || !payload.phone
        || !payload.roleId || !payload.position.id) {
        showToast('Заполните все поля', 'error');
        return;
    }

    // Пароль — только если введён
    let password = '';
    if (itemPasswordEl) {
        password = ('realValue' in itemPasswordEl)
            ? itemPasswordEl.realValue
            : itemPasswordEl.value;
    }
    if (password && password.length > 0) {
        if (password.length < 3) { showToast('Пароль минимум 3 символа', 'error'); return; }
        payload.password = password;
    }

    const id = itemIdEl.value;
    if (!id) { showToast('Не выбран пользователь', 'error'); return; }

    try {
        await apiFetch(`/api/users/${id}`, { method: 'PUT', body: payload });
        showToast('Обновлено', 'success');
        modal.classList.add('hidden');
        await load();
    } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
});

// ---- Удаление ----
async function removeItem(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    if (!confirm('Удалить пользователя #' + id + '?')) return;
    try {
        await apiFetch(`/api/users/${id}`, { method: 'DELETE' });
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
    if (!toastEl) { console.warn(message); return; }
    toastEl.textContent = message;
    toastEl.className = 'toast ' + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toastEl.classList.add('hidden'), 2500);
}

// ---- Старт ----
load();