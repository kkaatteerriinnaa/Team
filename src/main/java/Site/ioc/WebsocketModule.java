package Site.ioc;

import com.google.inject.AbstractModule;
import Site.ws.WebsocketConfigurator;

public class WebsocketModule extends AbstractModule {
    @Override
    protected void configure() {
        // інжекція у статичні поля
        requestStaticInjection(WebsocketConfigurator.class);
    }
}
