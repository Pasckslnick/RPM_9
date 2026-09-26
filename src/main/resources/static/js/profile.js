
const toastEl = document.getElementById('toast');


document.getElementById('logoutBtn').onclick = () => {
    localStorage.removeItem('positions_jwt');
    window.location.href = '/login.html';
};

const ADMIN_LINKS = [
    { title: 'Пользователи', href: '/user.html',     lockedForManager: true  },
    { title: 'Роли',         href: '/roles.html',     lockedForManager: true  },
    { title: 'Должности',    href: '/positions.html', lockedForManager: false },
    { title: 'Дилеры',       href: '/dealers.html',   lockedForManager: false },
    { title: 'Новости',      href: '/news.html',      lockedForManager: false }
];

async function loadProfile() {
    try {
        const p = await apiFetch('/api/auth/profile', { method: 'GET' });

        document.getElementById('pId').textContent        = p.id ?? '';
        document.getElementById('pUsername').textContent  = p.username ?? '';
        document.getElementById('pEmail').textContent     = p.email ?? '';
        document.getElementById('pPhone').textContent     = p.phone ?? '';
        document.getElementById('pPosition').textContent  = p.positionName || '—';

        const roleEl = document.getElementById('pRole');
        roleEl.textContent = p.roleName || '—';
        roleEl.className = 'role-badge ' +
            (p.isAdmin ? 'admin' : p.isManager ? 'manager' : 'user');

        renderPanel(p);
    } catch (e) {
        showToast('Ошибка загрузки профиля: ' + e.message, 'error');
    }
}

function renderPanel(p) {
    const panelTitle = document.getElementById('panelTitle');
    const panelLinks = document.getElementById('panelLinks');

    if (p.isAdmin) {
        panelTitle.textContent = 'Панель администратора';
    } else if (p.isManager) {
        panelTitle.textContent = 'Панель модератора';
    }
    const links = p.isAdmin || p.isManager
        ? ADMIN_LINKS
        : [
            { title: 'Новости',   href: '/news.html',      lockedForManager: false },
            { title: 'Дилеры',    href: '/dealers.html',   lockedForManager: false }];

    panelLinks.innerHTML = links.map(link => {

        return `
            <a class="panel-link" href="${link.href}">
                <span class="panel-link__title">${link.title}</span>
                <span class="panel-link__arrow">→</span>
            </a>
        `;
    }).join('');
}

let toastTimer;
function showToast(message, type = '') {
    toastEl.textContent = message;
    toastEl.className = 'toast ' + type;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toastEl.classList.add('hidden'), 2500);
}

// ---- Старт ----
loadProfile();