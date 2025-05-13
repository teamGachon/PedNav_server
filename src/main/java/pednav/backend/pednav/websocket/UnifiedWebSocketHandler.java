package pednav.backend.pednav.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pednav.backend.pednav.service.SyncService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class UnifiedWebSocketHandler extends TextWebSocketHandler {

    private final SyncService syncService;
    private final List<WebSocketSession> androidSessions = new CopyOnWriteArrayList<>();

    public UnifiedWebSocketHandler(SyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        androidSessions.add(session); // Android 클라이언트 저장
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        syncService.processIncomingJson(json, session);
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
