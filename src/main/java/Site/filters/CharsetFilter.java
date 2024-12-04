package Site.filters;

import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Singleton
public class CharsetFilter implements Filter {
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(                                        // Основний метод, викликається Servlet-API
                                                                 ServletRequest servletRequest,          // об'єкти запиту та відповіді, але !!!
                                                                 ServletResponse servletResponse,        // приходять як узагальнені (не HTTP)
                                                                 FilterChain filterChain                 // Ланцюг фільтрів для продовження
    ) throws IOException, ServletException {
        // Прямий хід - етап надходження запиту
        // Через те, що вхідні параметри мають узагальнений тип,
        // для певних задач іх треба перетворити
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        // з цього моменту це ті ж об'єкти щр й у сервлетах.
        String charsetName = StandardCharsets.UTF_8.name();
        req.setCharacterEncoding(charsetName);
        res.setCharacterEncoding(charsetName);
        // для передачі даних далі по ланці використовуємо атрибути
        req.setAttribute("charsetName", charsetName);
/*
        імітація авторизації користувача: протягом однієї сессії WS буде зберігатись
        саме це "ім'я" користувача
         */
        HttpSession httpSession = req.getSession();
        String user = (String) httpSession.getAttribute("user");
        if (user == null) {
            user = UUID.randomUUID().toString().substring(0, 4);
            httpSession.setAttribute("user", user);
        }
        req.setAttribute("user", user);
        // передача роботи по ланцюгу
        filterChain.doFilter(servletRequest, servletResponse);

        // Зворотній хід - етап надсилання відповіді
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
