// Case1DangerService.java
package pednav.backend.pednav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.domain.entity.DetectionResult;
import pednav.backend.pednav.dto.Case1DangerRequest;
import pednav.backend.pednav.repository.DataRepository;

@Service
@RequiredArgsConstructor
public class Case1DangerService {

    private final DataRepository repository;

    public String evaluateDanger(Case1DangerRequest req) {
        double score = (req.radarDetected() + req.soundDetected()) / 2.0;

        String danger;
        if (score > 0.75) danger = "HIGH";
        else if (score > 0.4) danger = "MEDIUM";
        else danger = "LOW";

        DetectionResult entity = new DetectionResult();
        entity.setTimestamp(req.timestamp());
        entity.setSoundDetected(req.soundDetected());
        entity.setRadarDetected(req.radarDetected());
        entity.setVelocity(null); // 없음
        entity.setDistance(null); // 없음
        entity.setDanger(danger);

        repository.save(entity);

        return danger;
    }
}
