/* ============================================================
   ZEEKR · News page (magazine style)
   ============================================================ */

const PAGE_SIZE = 9;

let state = { all: [], filtered: [], page: 0, users: [] };

let permissions = { canEdit: false, username: '' };

// ---- DOM ----
const grid         = document.getElementById('newsGrid');
const paginationEl = document.getElementById('pagination');
const toastEl      = document.getElementById('toast');

const modal        = document.getElementById('modal');
const modalTitle   = document.getElementById('modalTitle');
const form         = document.getElementById('newsForm');
const itemIdEl     = document.getElementById('itemId');
const itemTitleEl       = document.getElementById('itemTitle');
const itemContentEl     = document.getElementById('itemContent');
const itemImageEl       = document.getElementById('itemImage');
const itemPublishDateEl = document.getElementById('itemPublishDate');
const itemAuthorEl      = document.getElementById('itemAuthor');

const articleModal   = document.getElementById('articleModal');
const articleImg     = document.getElementById('articleImg');
const articleTitle   = document.getElementById('articleTitle');
const articleMeta    = document.getElementById('articleMeta');
const articleContent = document.getElementById('articleContent');

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
        permissions.role = String(me.role || '').replace(/^ROLE_/, '').toUpperCase();
        permissions.canEdit = permissions.role === 'ADMIN' || permissions.role === 'MANAGER';
        permissions.username = me.username || '';
    } catch (e) {
        permissions.canEdit = false;
    }
}

// ---- Users (для select автора) ----
async function loadUsers() {
    try {
        const response = await UsersApi.getAll();
        const users = Array.isArray(response) ? response : response?.content;
        state.users = Array.isArray(users) ? users : [];
        itemAuthorEl.innerHTML = '<option value="">— выберите автора —</option>' +
            state.users.map(u =>
                `<option value="${u.id}">${escapeHtml(u.username ?? ('#' + u.id))}</option>`
            ).join('');
    } catch (e) {
        state.users = [];
        itemAuthorEl.innerHTML = '<option value="">Не удалось загрузить авторов</option>';
        throw e;
    }
}

// ---- Load ----
async function load() {
    try {
        await loadPermissions();
        await loadUsers();

        const data = await NewsApi.getAll();
        state.all = Array.isArray(data) ? data : [];
        applyFilter();
    } catch (e) {
        const msg = (e && e.message) ? e.message : 'Ошибка загрузки';
        grid.innerHTML = `<p style="color:#ffb3b3;text-align:center;padding:40px;">${escapeHtml(msg)}</p>`;
    }
}

function applyFilter() {
    state.filtered = state.all.slice();
    state.page = 0;
    render();
}

// ---- Render ----
function render() {
    const addBtn = document.getElementById('addBtn');
    if (addBtn) addBtn.style.display = permissions.canEdit ? '' : 'none';

    const start = state.page * PAGE_SIZE;
    const pageItems = state.filtered.slice(start, start + PAGE_SIZE);

    if (!pageItems.length) {
        grid.innerHTML = `<p style="color:#7f7f7f;text-align:center;padding:40px;">Нет новостей</p>`;
        paginationEl.innerHTML = '';
        return;
    }

    grid.innerHTML = pageItems.map(n => {
        const author = getAuthorName(n.authorId);
        const bg = n.image ? `background-image: url('${escapeAttr(n.image)}')` : '';
        const excerpt = (n.content || '').slice(0, 220);
        const actions = permissions.canEdit
            ? `<div class="news-card__actions">
                   <button type="button" class="ghost news-edit" data-id="${n.id}">Изменить</button>
                   ${isAdmin() ? `<button type="button" class="ghost news-delete" data-id="${n.id}">Удалить</button>` : ''}
               </div>`
            : '';

        return `
            <article class="news-card" data-id="${n.id}">
                <div class="news-card__img" style="${bg}"></div>
                <div class="news-card__body">
                    <h3 class="news-card__title">${escapeHtml(n.title ?? '')}</h3>
                    <div class="news-card__meta">
                        ${escapeHtml(n.publishDate ?? '')}${author ? ' • ' + escapeHtml(author) : ''}
                    </div>
                    <div class="news-card__excerpt">${escapeHtml(excerpt)}</div>
                    <div class="news-card__more">Читать далее →</div>
                    ${actions}
                </div>
            </article>
        `;
    }).join('');

    renderPagination();
}

function getAuthorName(authorId) {
    const user = state.users.find(item => Number(item.id) === Number(authorId));
    return user ? user.username : (authorId ? `Автор #${authorId}` : '');
}

function isAdmin() {
    return String(permissions.role || '').replace(/^ROLE_/, '').toUpperCase() === 'ADMIN';
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

// ---- Клик по карточке → статья ----
grid.addEventListener('click', async (e) => {
    const edit = e.target.closest('.news-edit');
    if (edit) { await openEdit(Number(edit.dataset.id)); return; }
    const remove = e.target.closest('.news-delete');
    if (remove) { await removeNews(Number(remove.dataset.id)); return; }
    const card = e.target.closest('.news-card');
    if (!card) return;
    const id = Number(card.dataset.id);
    await openArticle(id);
});

paginationEl.addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-page]');
    if (!btn) return;
    state.page = Number(btn.dataset.page);
    render();
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

async function openArticle(id) {
    try {
        const item = await NewsApi.getById(id);
        const author = getAuthorName(item.authorId);

        articleImg.style.backgroundImage = item.image ? `url('${escapeAttr(item.image)}')` : 'none';
        articleTitle.textContent = item.title || '';
        articleMeta.textContent =
            (item.publishDate || '') + (author ? ' • ' + author : '');
        articleContent.textContent = item.content || '';

        articleModal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    } catch (e) {
        showToast('Ошибка: ' + e.message, 'error');
    }
}

function closeArticle() {
    articleModal.classList.add('hidden');
    document.body.style.overflow = '';
}

document.getElementById('articleClose').onclick = closeArticle;
articleModal.addEventListener('click', (e) => {
    if (e.target === articleModal) closeArticle();
});
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !articleModal.classList.contains('hidden')) closeArticle();
});

// ---- Форма (добавить/изменить) ----
document.getElementById('addBtn').onclick = () => {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    openCreate();
};
document.getElementById('cancelBtn').onclick = () => modal.classList.add('hidden');

function openCreate() {
    modalTitle.textContent = 'Добавить новость';
    itemIdEl.value = '';
    itemTitleEl.value = '';
    itemContentEl.value = '';
    itemImageEl.value = '';
    itemPublishDateEl.value = new Date().toISOString().slice(0, 10);
    itemAuthorEl.value = '';
    modal.classList.remove('hidden');
}

async function openEdit(id) {
    if (!permissions.canEdit) { showToast('Нет прав', 'error'); return; }
    try {
        const item = await NewsApi.getById(id);
        modalTitle.textContent = 'Изменить новость';
        itemIdEl.value = item.id;
        itemTitleEl.value = item.title ?? '';
        itemContentEl.value = item.content ?? '';
        itemImageEl.value = item.image ?? '';
        itemPublishDateEl.value = item.publishDate ?? '';
        itemAuthorEl.value = item.authorId ? String(item.authorId) : '';
        modal.classList.remove('hidden');
    } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const authorId = Number(itemAuthorEl.value);
    if (!authorId) { showToast('Выберите автора', 'error'); return; }

    const payload = {
        title:       itemTitleEl.value.trim(),
        content:     itemContentEl.value.trim(),
        image:       itemImageEl.value.trim(),
        publishDate: itemPublishDateEl.value,
        authorId
    };

    if (payload.title.length < 3) { showToast('Заголовок минимум 3 символа', 'error'); return; }
    if (payload.content.length < 10) { showToast('Содержание минимум 10 символов', 'error'); return; }
    if (!payload.image) { showToast('Укажите картинку', 'error'); return; }

    const id = itemIdEl.value;
    try {
        if (id) { await NewsApi.update(Number(id), payload); showToast('Обновлено', 'success'); }
        else    { await NewsApi.create(payload);              showToast('Создано',    'success'); }
        modal.classList.add('hidden');
        await load();
    } catch (e) { showToast('Ошибка сохранения: ' + e.message, 'error'); }
});

async function removeNews(id) {
    if (!isAdmin()) { showToast('Удаление доступно только ADMIN', 'error'); return; }
    if (!window.confirm('Удалить эту новость?')) return;
    try {
        await NewsApi.remove(id);
        showToast('Удалено', 'success');
        await load();
    } catch (e) {
        showToast('Ошибка удаления: ' + e.message, 'error');
    }
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
function escapeAttr(s) {
    // для использования внутри style="background-image: url('...')"
    return String(s).replaceAll("'", "\\'").replaceAll('"', '&quot;');
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