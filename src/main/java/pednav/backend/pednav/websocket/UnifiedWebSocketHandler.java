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

@Component
public class UnifiedWebSocketHandler extends TextWebSocketHandler {

    private Consumer<String> messageHandler;
    private final List<WebSocketSession> androidSessions = new CopyOnWriteArrayList<>();
    private final List<WebSocketSession> esp32Sessions = new CopyOnWriteArrayList<>();

    public UnifiedWebSocketHandler() {}

    @Autowired
    public void setMessageHandler(@Lazy SyncService syncService) {
        this.messageHandler = syncService::processIncomingJson;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("✅ WebSocket 연결 수립: " + session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload.contains("deviceType")) {
            if (payload.contains("ANDROID") && !androidSessions.contains(session)) {
                androidSessions.add(session);
            } else if (payload.contains("ESP32") && !esp32Sessions.contains(session)) {
                esp32Sessions.add(session);
            }
        } else if (messageHandler != null) {
            messageHandler.accept(payload);
        }
    }


    // 각 타입별 메시지 전송
    public void sendToDevice(String deviceType, String json) {
        List<WebSocketSession> targetSessions = deviceType.equals("ANDROID") ? androidSessions : esp32Sessions;
        for (WebSocketSession session : targetSessions) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
