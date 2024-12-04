package Site.ws;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Field;

/*
Конфігуратор - клас, який не лише "налаштовує" об'єкт,
а й відповідає за його створення. Якщо конфігуратор
зазначено, то й відповідальність за створення об'єкта-сервера
прокладається на нього. Це, зокрема, дозволяє використати
інжекцію залежностей.
 */
public class WebsocketConfigurator extends ServerEndpointConfig.Configurator {
    @Inject
    private static Injector injector; // !! static

    // Створення об'єкту -- вебсокету
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injector.getInstance(endpointClass);
    }
    // Налаштування - "втручання" у процес з'єднання клієнта і сервера

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*
        У даному методі request - це запит на з'єднання ( ініційований JS new WS())
        це не той запит, у якому наявні токени. Цей запит зберігається у request, але
        у приватному полі. Дістати його можна лише засобами рефлексії
         */
        HttpServletRequest httpRequest = null;
        for (Field field : request.getClass().getDeclaredFields()) {
            if (HttpServletRequest.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    httpRequest = (HttpServletRequest) field.get(request);
                } catch (IllegalAccessException ex) {
                    System.err.println("modifyHandshake: " + ex.getMessage());
                }
            }
        }
        if (httpRequest != null) {
            sec.getUserProperties().put("user", httpRequest.getAttribute("user"));
        }
    }
}
