<%@ page import="step.learning.dto.models.RegFormModel" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="step.learning.dto.models.RegistrationValidationModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Перевіряємо чи є повідомлення попередньої форми, формуємо значення для полів
    RegFormModel model = (RegFormModel) request.getAttribute("reg-model");
    String loginValue = model == null ? "" : model.getLogin();
    String nameValue = model == null ? "" : model.getName();
    String emailValue = model == null ? "" : model.getEmail();
    String birthdateValue = model == null ? "" : model.getBirthdateAsString();
    RegistrationValidationModel validationModel = model == null ? new RegistrationValidationModel() : model.getErrorMessages();
    String regMessage = (String) request.getAttribute("reg-message");
    String loginValid = validationModel.getLoignMessage().isEmpty() ? "valid" : "invalid";
    if (regMessage == null) {
        regMessage = "";
    }
//    Map<String, String> errors = model == null ? new HashMap<String, String>() : (HashMap<String, String>) model.getErrorMessages();
%>
<h2>Реєстрація користувача</h2>

<p><%=request.getAttribute("reg-message")%>
</p>
<div class="row">
    <form class="col s12" method="post" enctype="multipart/form-data" action="">
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">badge</i>
                <input value="<%= loginValue %>" name="reg-login" id="reg-login" type="text"
                       class="validate <% if (!validationModel.getLoignMessage().isEmpty()) { %>invalid<% } %>">
                <label for="reg-login">Логін на сайті</label>
                <% if (!validationModel.getLoignMessage().isEmpty()) { %>
                <span class="helper-text" data-error="<%=validationModel.getLoignMessage()%>">Helper text</span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">person</i>
                <input value="<%= nameValue %>" name="reg-name" id="reg-name" type="text"
                       class="validate <%=loginValid%>">
                <label for="reg-name">Реальне ім'я</label>
                <% if (!validationModel.getNameMessage().isEmpty()) { %>
                <span class="helper-text" data-error="<%=validationModel.getNameMessage()%>">Helper text</span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">lock</i>
                <input name="reg-password" id="reg-password" type="password" class="validate">
                <label for="reg-password">Пароль</label>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">lock_open</i>
                <input name="reg-repeat" id="reg-repeat" type="password" class="validate">
                <label for="reg-repeat">Повторіть пароль</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">alternate_email</i>
                <input value="<%= emailValue %>" name="reg-email" id="reg-email" type="email"
                       class="validate <% if (!validationModel.getEmailMessage().isEmpty()) { %>invalid<% } %>">
                <label for="reg-email">E-mail</label>
                <% if (!validationModel.getEmailMessage().isEmpty()) { %>
                <span class="helper-text" data-error="<%=validationModel.getEmailMessage()%>">Helper text</span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">cake</i>
                <input value="<%= birthdateValue %>" name="reg-birthdate" id="reg-birthdate" type="date"
                       class="validate <% if (!validationModel.getDateMessage().isEmpty()) { %>invalid<% } %>">
                <label for="reg-birthdate">Дата народження</label>
                <% if (!validationModel.getDateMessage().isEmpty()) { %>
                <span class="helper-text" data-error="<%=validationModel.getDateMessage()%>">Helper text</span>
                <% } %>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">receipt_long</i>
                <label> &emsp;
                    <input name="reg-rules" id="reg-rules" type="checkbox" class="filled-in validate">
                    <span>Не буду нічого порушувати</span>
                </label>
            </div>
            <div class="file-field input-field">
                <div class="btn deep-purple darken-4">
                    <i class="material-icons">account_box</i>
                    <input type="file" name="reg-avatar">
                </div>
                <div class="file-path-wrapper">
                    <input class="file-path validate" type="text"
                           placeholder="Зображення аватарка">
                </div>
            </div>
            <div class="input-field row right-align">
                <button class="waves-effect waves-light btn deep-purple darken-4"><i
                        class="material-icons right">how_to_reg</i>Реєстрація
                </button>
            </div>
        </div>
    </form>
</div>

<ul class="collection with-header">
    <li class="collection-header"><h4>Передача файлів через форми.</h4></li>
    <li class="collection-item">
        1. Передача файлів можлива лише методом POST та з кодвуанням
        пакету <code>multipart/form-data</code> (за замовчуванням, форма передається
        з іншим кодуванням <code>application/x-www-form-urlencoded</code>).
        Також переконуємось у наявності атрибута name у файловому інпуті.
    </li>
    <li class="collection-item">
        2. Приймання пакетів з боку сервера вимагає окремої обробки.
        Для цього вживаються додаткові модулі залежності. Наприклад,
        <a href="https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload">Apache Commons FileUpload</a>
    </li>
    <li class="collection-item">
        3. В ASP для роботи з файлами є інтерфейс IFromFile, його аналог в обраному
        пакеті - FileItem. У формі, окрім файлів, також передаються інші поля( у
        текстовому вигляді). Відповідно, результат розбору (парсингу) форми є
        дві колекції - файлів та полів. Для повернення єдиного результату ( з двох
        колекцій) слід зробити спільний інтерфейс.
    </li>
    <li class="collection-item"></li>
    <li class="collection-item"></li>
</ul>

