<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<h1>Інверсія управління у веб-застосунку</h1>
<ul class="collection with-header">
    <li class="collection-header"><h4>Реалізація IoC</h4></li>
    <li class="collection-item">
        1. Додаємо залежності:
        <a href="https://mvnrepository.com/artifact/com.google.inject/guice/6.0.0">
            Guice Core
        </a>
        та
        <a href="https://mvnrepository.com/artifact/com.google.inject.extensions/guice-servlet/6.0.0">
            Guice servlet
        </a>
    </li>
    <li class="collection-item">
        2. Створюємо пакет "ioc", у якому оголошуємо клас - слухач події
        утворення контексту (IocContextListener), спадковуємо від
        GuiceServletContextListener, імплементуємо метод getInjector
    </li>
    <li class="collection-item">
        3. Реєструємо цей клас у якості слухача (обробника) події
        створення контексту у web.xml, тут же реєструємо фільтр, але
        не наш, а Guice (com.google.inject.servlet.GuiceFilter)
    </li>
    <li class="collection-item">
        4. Створюємо конфігураційні модулі для інжектора:
        RouterModule (для налаштування сервлетів та фільтрів),
        ServicesModule (для налаштування служб)
        У класі IocContextListener зазначаємо об'єкти цих класів
    </li>
    <li class="collection-item">
        5. Переносимо конфігурацію (роутінг) фільтрів та сервлетів
        у RouterModule: видаляємо з web.xml всі записи (окрім нових)
        а також знімаємо анотації @WebFilter та @WebServlet
    </li>
    <li class="collection-item">
        6. !!! Додаємо анотацію @Singleton до усіх класів фільтрів та
        сервлетів
    </li>
    <li class="collection-item">
        7. Перевіряємо: hash(123) = <%=request.getAttribute("hash")%>
    </li>
    <li class="collection-item">
        8. Перевіряємо: randomHex(6) = <%=request.getAttribute("random")%>
    </li>
</ul>