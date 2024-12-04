<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String pageBody = (String) request.getAttribute("page-body");
    String context = request.getContextPath(); // /JavaWeb201 - Dekploy - App context
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Java web</title>
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <!--Import Google Icon Font-->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <%--    Site CSS--%>
    <link rel="stylesheet" href="<%=context%>/css/site.css?time=<%= new Date().getTime()%>"/>
    <%--    <script src="<%=context%>/js/site.js"></script>--%>
</head>
<body>
<nav>
    <div class="nav-wrapper purple darken-4">
        <!-- Modal Trigger -->
        <a class="left modal-trigger auth-icon" href="#auth-modal"><i class="material-icons">exit_to_app</i></a>

        <a href="<%= context %>/" class="left site-logo">Java Web201</a>

        <ul id="nav-me" class="right hide-on-med-and-down">
            <li <%="about.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/about">About</a></li>
            <li <%="filters.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/filters">Filters</a></li>
            <li <%="ioc.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/ioc">IoC</a></li>
            <li <%="db.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/db">DB</a></li>
            <li <%="spa.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/spa">SPA</a></li>
            <li <%="ws.jsp".equals(pageBody) ? "class='active'" : ""%>
            ><a href="<%=context%>/ws">WebSocket</a></li>
        </ul>
    </div>
</nav>

<%--<%=String.format("%s/%s", context, pageBody)%>--%>
<div class="container">
    <jsp:include page="<%= pageBody %>"/>
</div>


<footer class="page-footer purple darken-4">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Footer Content</h5>
                <p class="grey-text text-lighten-4">You can use rows and columns here to organize your footer
                    content.</p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Links</h5>
                <ul>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 1</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 2</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 3</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 4</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © <span id="currentYear">ff</span> Copyright Text
            <a class="grey-text text-lighten-4 right" href="#!">More Links</a>
        </div>
    </div>
</footer>


<!-- Modal Structure -->
<div id="auth-modal" class="modal">
    <div class="modal-content">
        <h4>Автентифікація на сайті</h4>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">badge</i>
                <input id="auth-login" type="text"
                       class="validate">
                <label for="auth-login">Логін на сайті</label>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">lock</i>
                <input id="auth-password" type="password" class="validate">
                <label for="auth-password">Пароль</label>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <b id="auth-message"></b>
        <a href="<%=context%>/signup" class="modal-close waves-effect deep-purple darken-4 btn-flat">Реєстрація</a>
        <button id="auth-sign-in" class="waves-effect deep-purple darken-2 btn-flat">Вхід</button>
    </div>
</div>


<!-- Compiled and minified JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%= context %>/js/site.js"></script>
<script src="<%= context %>/js/spa.js?time=<%= new Date().getTime()%>"></script>
<%--Спосіб антикешування, так робити не слід, якщо якісь ресурси не оновились, скоріш за все, це через кешовані дані--%>
<%--ctrl+f5 для повного оновлення сторінки--%>
<%--<!--Site JS-->--%>
<%--<script src="<%= context %>/js/site.js?time=<%= new Date().getTime()%>"></script>--%>
</body>
</html>
