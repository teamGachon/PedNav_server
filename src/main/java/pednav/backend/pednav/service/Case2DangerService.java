package pednav.backend.pednav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.domain.entity.DetectionResult;
import pednav.backend.pednav.external.FastApiClient;
import pednav.backend.pednav.repository.DataRepository;

@Service
@RequiredArgsConstructor
public class Case2DangerService {

    private final DataRepository repository;
    private final FastApiClient fastApiClient;

    public String evaluateAndSaveDanger(Long timestamp, Double radarDetected, byte[] audioPcm) {
        // 1. FastAPI (8002) 서버로 danger 예측 요청
        String danger = fastApiClient.predictDangerCase2(radarDetected, audioPcm);

        // 2. 결과를 엔티티로 저장
        DetectionResult entity = new DetectionResult();
        entity.setTimestamp(timestamp);
        entity.setRadarDetected(radarDetected);
        entity.setDanger(danger);
        entity.setAudioFileUrl(null); // 필요시 S3 경로 저장
        entity.setVelocity(null);     // Case2에서는 사용 안함
        entity.setDistance(null);     // Case2에서는 사용 안함
        entity.setSoundDetected(null); // Case2는 raw audio only

        repository.save(entity);

        return danger;
    }
}
