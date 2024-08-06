package swiftescaper.backend.swiftescaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swiftescaper.backend.swiftescaper.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Boolean existsLocationByUserIdAndTunnalId(Long userId, Long tunnalId);
    Location findLocationByUserIdAndTunnalId(Long userId, Long tunnalId);
}
