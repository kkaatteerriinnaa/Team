<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String connectionStatus = (String) request.getAttribute("connectionStatus");
%>
<p>
    <button id="db-create-button"
            class="waves-effect waves-light btn deep-purple darken-4">
        Create Phone Table
    </button>
<div class="input-field col s6">
    <input value id="user-name" name="user-name" type="text" class="validate">
    <label for="user-name">Ім'я</label>
    <span id="nameError" class="helper-text" data-error=""></span>
</div>
<div class="row">
    <div class="input-field col s10">
        <input placeholder="Телефон" name="user-phone" id="user-phone" type="text" class="validate">
        <%--        <label class="active" for="user-phone">Телефон</label>--%>
        <span id="phoneError" class="helper-text" data-error=""></span>

    </div>
    <div class="input-field col s2">
        <button id="number-generate-button"
                class="waves-effect waves-light btn deep-purple darken-4">
            <i class="material-icons right">autorenew</i>
            Generate
        </button>
    </div>
</div>
<button id="db-insert-button"
        class="waves-effect waves-light btn deep-purple darken-4">
    <i class="material-icons right">phone_iphone</i>
    Замовити дзвінок
</button>
<p id="lastId"></p>
<br/>
<u id="out"></u>
</p>
<div class="row">
    <button id="db-read-button"
            class="waves-effect waves-light btn deep-purple darken-4">
        <i class="material-icons right">view_list</i>
        Переглянути замовлення
    </button>
    <button id="db-readall-button"
            class="waves-effect waves-light btn deep-purple darken-4">
        <i class="material-icons right">delete</i>
        Переглянути всі замовлення
    </button>
    <div class="col s12" id="cardTable">
        <div class="card">
            <div class="card-content">
                <span class="card-title"><h4>Call me</h4></span>
                <div id="orderTable" class="row">
                </div>
            </div>
        </div>
    </div>
</div>

