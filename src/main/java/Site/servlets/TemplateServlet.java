package Site.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Site.dao.AuthTokenDao;
import Site.dto.enitities.AuthToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class TemplateServlet extends HttpServlet {
    private final static byte[] buffer = new byte[16384];
    private final Logger logger;
    private final AuthTokenDao authTokenDao;

    @Inject
    public TemplateServlet(Logger logger, AuthTokenDao authTokenDao) {
        this.logger = logger;
        this.authTokenDao = authTokenDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        Для сервлетів із множинним шаблоном шляху (route) актуальні:
        req.getServletPath() /tpl -постійна складова
        req.getPathInfo() /{template} -змінна складова
         */
        // Перевірка токена
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null) {
            sendResponse(resp, 400, "'Authorization' header required");
            return;
        }
        String authScheme = "Bearer ";
        if (!authHeader.startsWith(authScheme)) { // Bearer - схема авторизації з токеном
            sendResponse(resp, 400, "'Bearer' scheme required");
            return;
        }
        String token = authHeader.substring(authScheme.length());
        AuthToken authToken = authTokenDao.getTokenByBearer(token);
        if (authToken == null) {
            sendResponse(resp, 403, "token rejected");
            return;
        }
        // TODO:  вести журнал видачі ресурсів обмеженого доступу
        String requestedTemplate = req.getPathInfo();
        if (requestedTemplate.contains("..") || requestedTemplate.contains("//")) {
            resp.setStatus(404);
            return;
        }
        URL url = this.getClass().getClassLoader()
                .getResource("tpl" + requestedTemplate);
        if (url == null) {
            url = this.getClass().getClassLoader()
                    .getResource("tpl/spa-notfound.html");
            if (url == null) {
                resp.setStatus(404);
                return;
            }
        }
        File file = new File(url.getFile());
        if (!file.isFile()) {
            resp.setStatus(404);
            return;
        }
        try (InputStream fileStream = Files.newInputStream(file.toPath())) {
            int bytesRead;
            resp.setContentType(
                    URLConnection
                            .getFileNameMap()
                            .getContentTypeFor(requestedTemplate)
            );
            OutputStream respStream = resp.getOutputStream();
            while ((bytesRead = fileStream.read(buffer)) > 0) {
                respStream.write(buffer, 0, bytesRead);
            }
            respStream.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            resp.setStatus(500);
        }
    }

    private void sendResponse(HttpServletResponse resp, int status, String body) throws IOException {
        resp.setContentType("text/plain");
        resp.setStatus(status);
        resp.getWriter().print(body);
    }
}
