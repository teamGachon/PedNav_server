package swiftescaper.backend.swiftescaper.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swiftescaper.backend.swiftescaper.domain.Location;
import swiftescaper.backend.swiftescaper.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    public void sendLocation(Double lat, Double lng, Long tunnalId, Long userId) {
        Location location = Location.builder()
                .lat(lat)
                .lng(lng)
                .tunnalId(tunnalId)
                .userId(userId)
                .build();

        locationRepository.save(location);
        return;
    }
}
