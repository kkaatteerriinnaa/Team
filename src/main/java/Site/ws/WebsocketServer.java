package Site.ws;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import Site.dao.AuthTokenDao;
import Site.dao.ChatDao;
import Site.dto.enitities.AuthToken;
import Site.dto.enitities.ChatMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(
        value = "/chat", // url ws-сервера (ws://.../chat)
        configurator = WebsocketConfigurator.class
)
public class WebsocketServer {
    private final static Set<Session> sessions =
            Collections.synchronizedSet(
                    new HashSet<>()
            );
    private final ChatDao chatDao;
    private final AuthTokenDao authTokenDao;

    @Inject
    public WebsocketServer(ChatDao chatDao, AuthTokenDao authTokenDao) {
        this.chatDao = chatDao;
        this.authTokenDao = authTokenDao;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig sec) {
        session.getUserProperties().put("user", sec.getUserProperties().get("user"));
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        String command = jsonObject.get("command").getAsString();
        String data = jsonObject.get("data").getAsString();

        JsonObject respObject = new JsonObject();
        Object synchObject = new Object();
        switch (command) {
            case "auth":
                AuthToken authToken = authTokenDao.getTokenByBearer(data);
                if (authToken == null) {
                    sendToSession(session, 403, "token rejected");
                    try {
                        session.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    session.getUserProperties().put("auth", authToken.getSub());
                    session.getUserProperties().put("nik", authToken.getNik());
                    respObject.addProperty("statusMessage", "Accepted");
                    respObject.addProperty("data", authToken.getNik());
                    sendToSession(session, 202, respObject.toString());
                }
                break;
            case "chat":
                String sub = (String) session.getUserProperties().get("auth");
                String nik = (String) session.getUserProperties().get("nik");
                if (sub == null) {
                    sendToSession(session, 401, "Auth required");
                } else {
                    Thread addToDbThread = new Thread(() -> {
                        ChatMessage chatMessage = new ChatMessage(sub, data);
                        synchronized (synchObject) {
                            chatDao.add(chatMessage);
                        }
                        sendToAll(201, nik + ": " + data);
                    });
                    addToDbThread.start();
                    try {
                        addToDbThread.join();
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
                break;
            default:
                sendToSession(session, 405, "Command unrecognized");
        }

    }

    @OnError
    public void onError(Throwable ex, Session session) {
        System.err.println("onError" + ex.getMessage());
    }

    private void sendToSession(Session session, int status, String data) {
        JsonObject respObject = new JsonObject();

        respObject.addProperty("status", status);
        respObject.addProperty("data", data);
        try {
            session.getBasicRemote().sendText(respObject.toString());
        } catch (IOException ex) {
            System.err.println("sendToSession: " + ex.getMessage());
        }
    }

    private void sendToAll(int status, String data) {
        for (Session session : sessions) {
            sendToSession(session, status, data);
        }
    }
}
