
const params = new URLSearchParams(window.location.search);
const cardId = Number(params.get('id'));

let state = {
    card: null,
    items: [],
    complecttions: []
};
let permissions = { canEdit: false, username: '' };

const titleEl     = document.getElementById('cardTitle');
const metaEl      = document.getElementById('cardMeta');
const itemsBody   = document.getElementById('itemsBody');
const totalPriceEl= document.getElementById('totalPrice');
const toastEl     = document.getElementById('toast');
const addItemBtn  = document.getElementById('addItemBtn');
const orderBtn  = document.getElementById('orderBtn');

const itemModal      = document.getElementById('itemModal');
const itemModalTitle = document.getElementById('itemModalTitle');
const itemForm       = document.getElementById('itemForm');
const itemIdEl       = document.getElementById('itemId');
const itemCardIdEl   = document.getElementById('itemCardId');
const itemCompEl     = document.getElementById('itemComplecttion');
const itemQuantityEl = document.getElementById('itemQuantity');
const itemPriceEl    = document.getElementById('itemPrice');

const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.onclick = () => {
        localStorage.removeItem('positions_jwt');
        window.location.href = '/login.html';
    };
}

async function loadPermissions() {
    try {
        const me = await AuthMe.get();
        permissions.canEdit = true;  // корзину может менять её владелец или админ
        permissions.isAdmin = !!(me.isAdmin || me.isManager);
        permissions.username = me.username || '';
    } catch (e) { permissions.canEdit = false; }
}

async function loadCard() {
    if (!cardId) {
        titleEl.textContent = 'Корзина не найдена';
        return;
    }
    try {
        await loadPermissions();

        const card = await CardsApi.getById(cardId);
        state.card = card;

        const comps = await CarComplectApi.getAll();
        state.complecttions = Array.isArray(comps) ? comps : [];

        const items = await apiFetch(`/api/card-details/by-card/${cardId}`, { method: 'GET' });
        state.items = Array.isArray(items) ? items : [];

        renderHeader();
        renderItems();
    } catch (e) {
        titleEl.textContent = 'Ошибка загрузки';
        metaEl.textContent = e.message;
    }
}

function renderHeader() {
    const c = state.card;
    const userName = c.user && c.user.username ? c.user.username : '—';
    titleEl.textContent = 'Корзина #' + c.id;
    metaEl.textContent  = `${userName} · ${c.createdAt || ''} · ${c.status || ''}`;

    if (addItemBtn) {
        addItemBtn.style.display = (permissions.canEdit || permissions.isAdmin) ? '' : 'none';
    }

}

function renderItems() {
    if (!itemsBody) return;

    itemsBody.innerHTML = state.items.map(it => {
        const comp = it.complecttion || {};
        const car  = comp.carModel || {};
        const sum  = (it.price ?? 0) * (it.quantity ?? 0);

        return `
            <tr>
                <td>${it.id ?? ''}</td>
                <td>${escapeHtml(car.modelName ?? '—')}</td>
                <td>${escapeHtml(comp.configurationName ?? '—')}</td>
                <td>${formatPrice(it.price)} ₽</td>
                <td>${it.quantity ?? '—'}</td>
                <td>${formatPrice(sum)} ₽</td>
                <td class="actions">
                    <button data-action="edit" data-id="${it.id}" class="ghost">Изменить</button>
                    <button data-action="delete" data-id="${it.id}" class="danger">Удалить</button>
                </td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="7" style="text-align:center;color:#7f7f7f;padding:24px">Позиций нет</td></tr>`;

    const total = state.items.reduce((s, it) => s + (it.price ?? 0) * (it.quantity ?? 0), 0);
    if (totalPriceEl) totalPriceEl.textContent = formatPrice(total);
}

if (itemsBody) {
    itemsBody.addEventListener('click', async (e) => {
        const btn = e.target.closest('button[data-action]');
        if (!btn) return;
        const id = Number(btn.dataset.id);
        const action = btn.dataset.action;

        if (action === 'edit')   openEditItem(id);
        if (action === 'delete') await removeItem(id);
    });
}

function fillComplecttionSelect() {
    if (!itemCompEl) return;
    itemCompEl.innerHTML = '<option value="">— выберите комплектацию —</option>' +
        state.complecttions.map(c => {
            const car = c.carModel || {};
            const label = (car.modelName ? car.modelName + ' · ' : '') +
                (c.configurationName || ('#' + c.id)) +
                ' — ' + formatPrice(c.price) + ' ₽';
            return `<option value="${c.id}" data-price="${c.price}">${escapeHtml(label)}</option>`;
        }).join('');
}

if (addItemBtn) {
    addItemBtn.onclick = () => {
        itemModalTitle.textContent = 'Добавить комплектацию';
        itemIdEl.value = '';
        itemCardIdEl.value = cardId;
        fillComplecttionSelect();
        itemCompEl.value = '';
        itemQuantityEl.value = 1;
        itemPriceEl.value = '';
        itemModal.classList.remove('hidden');
    };
}

if (orderBtn) {
    orderBtn.onclick = async () => {
        if (!state.items.length) {
            showToast('Корзина пуста', 'error');
            return;
        }

        const c = state.card;

        const payload = {
            user:      { id: c.user.id },
            createdAt: c.createdAt,
            status:    'Оформлена'
        };

        try {
            await CardsApi.update(cardId, payload);
            showToast('Корзина оформлена', 'success');

            await loadCard();
        } catch (e) {
            showToast('Ошибка: ' + e.message, 'error');
            console.error(e);
        }
    };
}

if (itemCompEl) {
    itemCompEl.addEventListener('change', () => {
        const opt = itemCompEl.selectedOptions[0];
        if (opt && opt.dataset.price) {
            itemPriceEl.value = opt.dataset.price;
        }
    });
}

const itemCancelBtnEl = document.getElementById('itemCancelBtn');
if (itemCancelBtnEl) {
    itemCancelBtnEl.onclick = () => itemModal.classList.add('hidden');
}

function openEditItem(id) {
    const it = state.items.find(x => x.id === id);
    if (!it) return;
    itemModalTitle.textContent = 'Изменить позицию';
    itemIdEl.value = it.id;
    itemCardIdEl.value = cardId;
    fillComplecttionSelect();
    itemCompEl.value = it.complecttion && it.complecttion.id ? String(it.complecttion.id) : '';
    itemQuantityEl.value = it.quantity ?? 1;
    itemPriceEl.value = it.price ?? '';
    itemModal.classList.remove('hidden');
}

if (itemForm) {
    itemForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const compId = Number(itemCompEl.value);
        const qty    = Number(itemQuantityEl.value);
        const price  = Number(itemPriceEl.value);

        if (!compId || !qty || qty < 1 || !price || price <= 0) {
            showToast('Заполните все поля', 'error');
            return;
        }

        const payload = {
            card:        { id: cardId },
            complecttion:{ id: compId },
            quantity:    qty,
            price:       price
        };

        const id = itemIdEl.value;
        try {
            if (id) {
                await CardDetailsApi.update(Number(id), payload);
                showToast('Обновлено', 'success');
            } else {
                await CardDetailsApi.create(payload);
                showToast('Добавлено', 'success');
            }
            itemModal.classList.add('hidden');
            await loadCard();
        } catch (e) { showToast('Ошибка: ' + e.message, 'error'); }
    });
}

async function removeItem(id) {
    if (!confirm('Удалить позицию #' + id + '?')) return;
    try {
        await CardDetailsApi.remove(id);
        showToast('Удалено', 'success');
        await loadCard();
    } catch (e) { showToast('Ошибка удаления: ' + e.message, 'error'); }
}

function formatPrice(v) {
    if (v == null) return '0';
    return Number(v).toLocaleString('ru-RU');
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

loadCard();