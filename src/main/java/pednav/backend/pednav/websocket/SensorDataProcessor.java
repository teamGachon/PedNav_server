
package pednav.backend.pednav.websocket;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.PartialData;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.service.DangerEvaluator;

@Component
public class SensorDataProcessor {

    private final DataRepository repository;
    private final DangerEvaluator dangerEvaluator;  // ✅ 의존성 분리
    private final UnifiedWebSocketHandler webSocketHandler;

    public SensorDataProcessor(
            DataRepository repository,
            DangerEvaluator dangerEvaluator,  // ✅ 더 이상 SyncService 아님
            UnifiedWebSocketHandler webSocketHandler
    ) {
        this.repository = repository;
        this.dangerEvaluator = dangerEvaluator;
        this.webSocketHandler = webSocketHandler;
    }

    public void process(Long timestamp, SensorDataBuffer.SensorPayload payload) {
        PartialData data = new PartialData(timestamp);
        data.setVehicleDetected(payload.vehicleDetected.doubleValue());
        data.setVelocity(payload.velocity.doubleValue());
        data.setDistance(payload.distance.doubleValue());

        String danger = dangerEvaluator.evaluateDanger(data);  // ✅ 사용 방식 동일
        data.setDanger(danger);

        repository.save(data.toEntity());

        String resultJson = "{\"danger\":\"" + danger + "\"}";
        webSocketHandler.sendToAndroidClients(resultJson);

    }
}