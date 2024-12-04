package Site.ioc;

import Site.filters.CharsetFilter;
import Site.servlets.*;
import com.google.inject.servlet.ServletModule;
import step.learning.filters.*;
import step.learning.servlets.*;

public class RouterModule extends ServletModule {
    @Override
    protected void configureServlets() {
        // Третій спосіб конфігурування фільтрів та сервлетів - IoC
        filter("/*").through(CharsetFilter.class);

        serve("/").with(HomeServlet.class);
        serve("/about").with(JspServlet.class);
        serve("/auth").with(AuthServlet.class);
        serve("/db").with(DbServlet.class);
        serve("/filters").with(FiltersServlet.class);
        serve("/ioc").with(IocServlet.class);
        serve("/signup").with(SignupServlet.class);
        serve("/spa").with(SpaServlet.class);
        serve("/tpl/*").with(TemplateServlet.class);
        serve("/ws").with(WsServlet.class);
    }
}
