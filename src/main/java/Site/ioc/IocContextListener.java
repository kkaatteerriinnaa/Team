package Site.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class IocContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new WebsocketModule(),
                new RouterModule(),
                new ServicesModule(),
                new LoggerModule()
        );
    }
}
