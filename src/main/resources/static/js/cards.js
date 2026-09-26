/* ============================================================
   ZEEKR · Cards list page
   ============================================================ */

const PAGE_SIZE = 10;

let state = { all: [], filtered: [], page: 0 };
let permissions = { canEdit: false, username: '' };

const tbody        = document.getElementById('cardsBody');
const paginationEl = document.getElementById('pagination');
const searchInput  = document.getElementById('searchInput');
const toastEl      = document.getElementById('toast');

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
    } catch (e) { permissions.canEdit = false; }
}

// ---- Загрузка ----
async function load() {
    try {
        await loadPermissions();
        const data = await CardsApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:#ffb3b3;padding:24px;">
                ${escapeHtml(msg)}
            </td></tr>`;
        }
    }
}

function applyFilter() {
    if (!searchInput) return;
    const kw = searchInput.value.trim().toLowerCase();
    state.filtered = kw
        ? state.all.filter(c => (c.status || '').toLowerCase().includes(kw))
        : state.all.slice();
    state.page = 0;
    render();
}

// ---- Отрисовка ----
function render() {
    if (!tbody) return;
    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    tbody.innerHTML = pageItems.map(c => {
        const userName = c.user && c.user.username ? c.user.username : '—';
        const detailsCount = Array.isArray(c.details) ? c.details.length : '—';

        return `
            <tr>
                <td>${c.id ?? ''}</td>
                <td>${escapeHtml(userName)}</td>
                <td>${escapeHtml(c.createdAt ?? '')}</td>
                <td>${escapeHtml(c.status ?? '')}</td>
                <td>${detailsCount}</td>
                <td class="actions">
                    <button data-action="open" data-id="${c.id}" class="primary">Открыть</button>
                    ${permissions.canEdit ? `
                        <button data-action="delete" data-id="${c.id}" class="danger">Удалить</button>
                    ` : ''}
                </td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="6" style="text-align:center;color:#7f7f7f;padding:24px">Нет данных</td></tr>`;

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

// ---- Клики ----
if (tbody) {
    tbody.addEventListener('click', async (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;
        const id = Number(btn.dataset.id);
        const action = btn.dataset.action;

        if (action === 'open')   window.location.href = `/card.html?id=${id}`;
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

// ---- Удаление ----
async function removeItem(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    if (!confirm('Удалить корзину #' + id + '?')) return;
    try {
        await CardsApi.remove(id);
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

load();