/* Script for SPA page + Auth functions */
document.addEventListener("DOMContentLoaded", () => {
    M.Modal.init(document.querySelectorAll('.modal'), {
        opacity: .5,
        inDuration: 200,
        outDuration: 200,
        onOpenStart: onModalopens,
    });
    const authSignInButton = document.getElementById("auth-sign-in");
    if (authSignInButton) {
        authSignInButton.addEventListener('click', authSignInButtonClick)
    } else {
        console.error("#auth-sign-in not found");
    }
    // Token verification
    const spaTokenStatus = document.getElementById("spa-token-status");

    const logoutButton = document.getElementById("spa-logout");

    const spaTokenExp = document.getElementById("spa-token-exp");

    if (spaTokenStatus && spaTokenExp) {
        const token = window.localStorage.getItem('token');
        spaTokenStatus.innerText = 'Встановлено ' + token
        if (token) {
            const tokenObject = JSON.parse(atob(token));
            // TODO: перевірити на правильність декодування та дійсність токена
            spaTokenStatus.innerText = 'Встановлено ' + tokenObject.jti;
            spaTokenExp.innerText = "Дійсний до " + tokenObject.exp;
            logoutButton.style.display = '';
            logoutButton
                .addEventListener('click', logoutClick);
            const appContext = getAppContext();
            fetch(`${appContext}/tpl/spa-auth.html`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }).then(r => r.text()).then(t =>
                document
                    .querySelector('auth-part')
                    .innerHTML = t);
        } else {
            spaTokenStatus.innerText = 'Не встановлено';
            logoutButton.style.display = 'none';
        }
    }

    const spaGetData = document.getElementById('spa-get-data');
    if (spaGetData) {
        spaGetData.addEventListener('click', spaGetDataClick);
    }
    const spaGetProduct = document.getElementById('spa-get-products');
    if (spaGetProduct) {
        spaGetProduct.addEventListener('click', spaGetProductClick);
    }
    const spaGetProtected = document.getElementById('spa-get-protected');
    if (spaGetProtected) {
        spaGetProtected.addEventListener('click', spaGetProtectedClick);
    }
    const spaNotFound = document.getElementById('spa-notfound');
    if (spaNotFound) {
        spaNotFound.addEventListener('click', spaNotFoundClick);
    }
});

function spaNotFoundClick() {
    const appContext = getAppContext();
    const token = window.localStorage.getItem('token');
    fetch(`${appContext}/tpl/spa-aut2h.html`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }).then(r => r.text()).then(t =>
        document
            .querySelector('auth-part')
            .innerHTML = t);
}

function spaGetProtectedClick() {
    const appContext = getAppContext();
    const token = window.localStorage.getItem('token');
    fetch(`${appContext}/tpl/spa-auth.html`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }).then(r => r.text()).then(t =>
        document
            .querySelector('auth-part')
            .innerHTML = t);


}

function spaGetProductClick() {
    const appContext = getAppContext();
    const token = window.localStorage.getItem('token');
    fetch(`${appContext}/tpl/spa-products.html`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }).then(r => r.text()).then(t =>
        document
            .querySelector('auth-part')
            .innerHTML = t);


}

function spaGetDataClick() {
    console.log("Data spaGetDataClick");
    const appContext = getAppContext();
    const token = window.localStorage.getItem('token');
    fetch(`${appContext}/tpl/NP.png`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(r => r.blob())
        .then(b => {
            const blobUrl = URL.createObjectURL(b);
            document.querySelector('auth-part').innerHTML =
                `<img src="${blobUrl}" height="250" alt="no-image"/>`;
        })


}

function logoutClick(e) {
    e.target.style.display = 'none'
    window.localStorage.removeItem('token');
    window.location.reload();
}

function onModalopens() {
    [authLogin, authPassword, authMessage] = getAuthElements();
    authLogin.value = '';
    authPassword.value = '';
    authMessage.innerText = '';
}

function getAppContext() {
    return '/' + window.location.pathname.split('/')[1];
}

function authSignInButtonClick() {
    [authLogin, authPassword, authMessage] = getAuthElements();
    if (authLogin.value.length === 0) {
        authMessage.innerText = "Логін не може бути порожнім"
    }
    const appContext = getAppContext();
    fetch(`${appContext}/auth?login=${authLogin.value}&password=${authPassword.value}`, {
        method: 'GET'
    }).then(r => {
        if (r.status !== 200) {
            authMessage.innerText = "Автентифікацію відхилено"
        } else {
            r.text().then(base64encodedText => {
                console.log(base64encodedText)
                const token = JSON.parse(atob(base64encodedText));

                if (typeof token.jti === 'undefined') {
                    authMessage.innerText = "Помилка одержання токену";
                    return;
                }
                window.localStorage.setItem('token', base64encodedText);
                if (window.location.pathname === `${appContext}/spa`) {
                    window.location.reload();
                } else {
                    document.location.pathname = `${appContext}/spa`;
                }
            });
        }
    });
}

function getAuthElements() {
    const authLogin = document.getElementById("auth-login");
    if (!authLogin) {
        throw '#auth-login not found';
    }
    const authPassword = document.getElementById("auth-password");
    if (!authPassword) {
        throw '#auth-password not found';
    }
    const authMessage = document.getElementById("auth-message");
    if (!authMessage) {
        throw '#auth-message not found';
    }
    return [authLogin, authPassword, authMessage];
}
