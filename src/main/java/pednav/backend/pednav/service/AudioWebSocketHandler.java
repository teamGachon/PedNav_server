package pednav.backend.pednav.service;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private static final String AUDIO_FILE_PATH = "received_audio.pcm"; // 파일 저장 경로

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WebSocket 연결 성공: " + session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        byte[] audioData = message.getPayload().array(); // PCM 데이터 추출
        System.out.println("Received audio data: " + audioData.length + " bytes");

        // PCM 데이터를 파일로 저장
        try (FileOutputStream fileOutputStream = new FileOutputStream(AUDIO_FILE_PATH, true)) {
            fileOutputStream.write(audioData);
        }

        // 여기서 FCM 알림을 전송하거나, 추가적인 데이터를 처리할 수 있음
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("WebSocket 연결 종료: " + session.getId() + " 상태: " + status);
    }
}