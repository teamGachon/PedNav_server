package swiftescaper.backend.swiftescaper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swiftescaper.backend.swiftescaper.service.FCMService;

@RestController
@RequestMapping("/api/fcm")
public class FCMController {

    @Autowired
    private FCMService fcmService;

    @PostMapping("/send")
    public String sendNotification(@RequestParam String token,
                                   @RequestParam String title,
                                   @RequestParam String body) {
        fcmService.sendNotification(token, title, body);
        return "Notification sent successfully!";
    }
}
