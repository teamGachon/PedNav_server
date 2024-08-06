package swiftescaper.backend.swiftescaper.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swiftescaper.backend.swiftescaper.domain.Location;
import swiftescaper.backend.swiftescaper.repository.LocationRepository;
import swiftescaper.backend.swiftescaper.service.LocationService;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;

    @PostMapping("/send")
    public String sendNotification(@Parameter(description = "Latitude of the location", required = true) @RequestParam Double lat,
                                   @Parameter(description = "Longitude of the location", required = true) @RequestParam Double lng,
                                   @Parameter(description = "ID of the location", required = true) @RequestParam Long tunnalId,
                                   @Parameter(description = "ID", required = true) @RequestParam Long userId) {
        if(locationRepository.existsLocationByUserIdAndTunnalId(userId, tunnalId)) {
            Location location = locationRepository.findLocationByUserIdAndTunnalId(userId, tunnalId);
            location.setLat(lat);
            location.setLng(lng);
            locationRepository.save(location);
        }else {
            locationService.sendLocation(lat, lng, tunnalId, userId);
        }
        return "Notification sent successfully!";
    }
}
