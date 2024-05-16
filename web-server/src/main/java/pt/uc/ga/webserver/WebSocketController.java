package pt.uc.ga.webserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pt.uc.ga.IGateway;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

@Controller
public class WebSocketController extends TextWebSocketHandler {
    private final String RMI_HOST = "localhost";
    private final int RMI_GATEWAY_PORT = 1099;

    private final ArrayList<WebSocketSession> sessions = new ArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);

        try {
            while (true) {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_GATEWAY_PORT);
                IGateway gateway = (IGateway) registry.lookup("googol");
                String reply = gateway.getAdminPage(true);

                if (!session.isOpen())
                    break;

                try {
                    session.sendMessage(new TextMessage(reply));
                } catch (IOException e) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            sessions.remove(session);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}