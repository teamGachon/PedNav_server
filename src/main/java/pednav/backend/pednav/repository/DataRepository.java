package pednav.backend.pednav.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pednav.backend.pednav.domain.entity.DetectionResult;

public interface DataRepository extends JpaRepository<DetectionResult, Long> {
}

