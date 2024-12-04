<%@ page contentType="text/html;charset=UTF-8" %>
<h1>SPA</h1>
<p>
    Автентифікація та авторизація - за допомогою токенів здійснюється
    наступним чином:<br/>
</p>
<ul class="collection">
    <li class="collection-item">
        1. користувач вводить логін та пароль, формується асинхронний запит
        до API авторизації, у відповідь отримується токен.
    </li>
    <li class="collection-item">
        2. токен перевіряється на цілісніть та зберігається у локальному
        сховищі. Подальші запити включають одержаний токен до
        заголовків
    </li>
    <li class="collection-item">
        3. Наявність токену на сторінці: <b id="spa-token-status"></b>
    </li>
    <li class="collection-item">
        4. Термін дії токену на сторінці до: <b id="spa-token-exp"></b>
    </li>
    <li class="collection-item">
        5. Активність токену: <b id="spa-token-activity"></b>
    </li>
</ul>
<auth-part></auth-part>
<div class="row">
    <div class="col s2">
        <button class="btn deep-purple darken-2" id="spa-get-data">Дані</button>
    </div>
    <div class="col s2">
        <button class="btn deep-purple darken-2" id="spa-get-protected">ІзОД</button>
    </div>
    <div class="col s2">
        <button class="btn deep-purple darken-2" id="spa-get-products">Товари</button>
    </div>
    <div class="col s2">
        <button class="btn deep-purple darken-2" id="spa-notfound">404</button>
    </div>
    <div class="col s2">
        <button class="btn deep-purple darken-4" id="spa-logout">Вихід</button>
    </div>

</div>
