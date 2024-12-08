package pednav.backend.pednav.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FCMServiceImpl implements FCMService {

    @Override
    public void sendBatchNotification(List<String> tokens, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        System.out.println("알림 전송 성공: " + response.getSuccessCount());
        System.out.println("알림 전송 실패: " + response.getFailureCount());
    }
}
