package pednav.backend.pednav.service;


import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface FCMService {
    void sendBatchNotification(List<String> tokens, String title, String body) throws FirebaseMessagingException;
}

