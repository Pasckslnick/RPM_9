
//'positions_jwt'

const TOKEN_KEY = 'positions_jwt';


const AuthMe = {
    get() {
        return apiFetch('/api/auth/me', { method: 'GET' });
    }
};

window.AuthMe = AuthMe;

function getToken() {
    return localStorage.getItem(TOKEN_KEY) || '';
}

function setToken(token) {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function decodeJwt(token) {
    try {
        const payload = token.split('.')[1];
        const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(json);
    } catch (_) {
        return null;
    }
}

function isTokenExpired(token) {
    const data = decodeJwt(token);
    if (!data || !data.exp) return true;
    return Date.now() >= data.exp * 1000;
}

function redirectToError(status, path) {
    const params = new URLSearchParams();
    if (status) params.set('status', status);
    if (path)   params.set('path', path);
    const qs = params.toString();
    window.location.href = '/error' + (qs ? '?' + qs : '');
}

function redirectToLogin() {
    window.location.href = '/login.html';
}

async function apiFetch(url, options = {}) {
    const headers = {
        'Accept': 'application/json',
        ...(options.headers || {})
    };

    const token = getToken();
    if (token) {
        if (isTokenExpired(token)) {
            clearToken();
            redirectToError(401, url);
            throw new Error('HTTP 401: token expired');
        }
        headers['Authorization'] = `Bearer ${token}`;
    }

    if (options.body && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, { ...options, headers });

    if (response.status === 204) return null;

    if (!response.ok) {

        if (response.status === 403) {
            redirectToError(403, url);
        }

        if (response.status === 401) {
            clearToken();
            redirectToLogin();
        }

        let message = `HTTP ${response.status}`;
        try {
            const data = await response.json();
            if (data.message) message = data.message;
            else if (data.error) message = data.error;
        } catch (_) { /* ignore */ }

        const err = new Error(message);
        err.status = response.status;
        throw err;
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}


function makeCrudApi(baseUrl) {
    return {
        getAll()        { return apiFetch(baseUrl,                  { method: 'GET' }); },
        getById(id)     { return apiFetch(`${baseUrl}/${id}`,       { method: 'GET' }); },
        create(item)    { return apiFetch(baseUrl,                  { method: 'POST', body: item }); },
        update(id, item){ return apiFetch(`${baseUrl}/${id}`,       { method: 'PUT',  body: item }); },
        remove(id)      { return apiFetch(`${baseUrl}/${id}`,       { method: 'DELETE' }); }
    };
}


const PositionsApi   = makeCrudApi('/api/positions');
const NewsApi        = makeCrudApi('/api/news');
const UsersApi       = makeCrudApi('/api/users');
const UserRolesApi   = makeCrudApi('/api/roles');
const CarModelsApi   = makeCrudApi('/api/car-models');
const CarComplectApi = makeCrudApi('/api/car-complecttions');
const CardsApi       = makeCrudApi('/api/cards');
const CardDetailsApi = makeCrudApi('/api/card-details');
const DealersApi     = makeCrudApi('/api/dealers');
const TestDrivesApi  = makeCrudApi('/api/test-drives');


const AuthApi = {
    login(username, password) {
        return apiFetch('/api/auth/login', {
            method: 'POST',
            body: { username, password }
        });
    },
    register(payload) {
        return apiFetch('/api/auth/register', {
            method: 'POST',
            body: payload
        });
    }
};





window.getToken        = getToken;
window.setToken        = setToken;
window.clearToken      = clearToken;
window.apiFetch        = apiFetch;
window.decodeJwt       = decodeJwt;
window.isTokenExpired  = isTokenExpired;
window.redirectToError = redirectToError;
window.redirectToLogin = redirectToLogin;
window.PositionsApi    = PositionsApi;
window.NewsApi         = NewsApi;
window.UsersApi        = UsersApi;
window.UserRolesApi    = UserRolesApi;
window.CarModelsApi    = CarModelsApi;
window.CarComplectApi  = CarComplectApi;
window.CardsApi        = CardsApi;
window.CardDetailsApi  = CardDetailsApi;
window.DealersApi      = DealersApi;
window.TestDrivesApi   = TestDrivesApi;
window.AuthApi         = AuthApi;