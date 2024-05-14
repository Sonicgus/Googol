package pt.uc.ga.webserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pt.uc.ga.IGateway;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class WebSocketController extends TextWebSocketHandler {
    private final String RMI_HOST = "localhost";
    private final int RMI_GATEWAY_PORT = 1099;

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);

        new Thread(() -> {
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
                    IGateway gateway = (IGateway) registry.lookup("googol");
                    String reply = gateway.getAdminPage(true);
                    for (WebSocketSession webSocketSession : sessions) {
                        synchronized (webSocketSession) {
                            webSocketSession.sendMessage(new TextMessage(reply));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Handle incoming messages if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}