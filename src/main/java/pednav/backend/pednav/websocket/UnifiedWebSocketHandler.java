package pednav.backend.pednav.websocket;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pednav.backend.pednav.service.SyncService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class UnifiedWebSocketHandler extends TextWebSocketHandler {

    private Consumer<String> messageHandler;
    private final List<WebSocketSession> androidSessions = new CopyOnWriteArrayList<>();

    public UnifiedWebSocketHandler() {} // ✅ buffer 제거

    @Autowired
    public void setMessageHandler(@Lazy SyncService syncService) {
        this.messageHandler = syncService::processIncomingJson;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        androidSessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (messageHandler != null) {
            messageHandler.accept(message.getPayload());
        }
    }

    public void sendToAndroidClients(String resultJson) {
        for (WebSocketSession session : androidSessions) {
            try {
                session.sendMessage(new TextMessage(resultJson));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
