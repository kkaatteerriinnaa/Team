package Site.servlets;

import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class SpaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("page-body", "spa.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp")
                .forward(req, resp); // ~ return View()
    }
}
/*
SPA - Single Page Application - схема побудови браузерного застосунку,
у якому перехід між різними сторінками "імітується",
тобто сторінка залишається та ж сама, але оновлюються її частини
(контент)
 */

