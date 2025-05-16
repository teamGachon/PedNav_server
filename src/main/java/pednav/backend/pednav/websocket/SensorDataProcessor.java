
package pednav.backend.pednav.websocket;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.PartialData;
import pednav.backend.pednav.external.FastApiClient;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.service.DangerEvaluator;

@Component
public class SensorDataProcessor {

    private final DataRepository repository;
    private final UnifiedWebSocketHandler webSocketHandler;
    private final FastApiClient fastApiClient;

    public SensorDataProcessor(
            DataRepository repository,
            FastApiClient fastApiClient,
            UnifiedWebSocketHandler webSocketHandler
    ) {
        this.repository = repository;
        this.fastApiClient = fastApiClient;
        this.webSocketHandler = webSocketHandler;
    }

    public void process(Long timestamp, SensorDataBuffer.SensorPayload payload) {
        PartialData data = new PartialData(timestamp);
        data.setVehicleDetected(payload.vehicleDetected.doubleValue());
        data.setVelocity(payload.velocity.doubleValue());
        data.setDistance(payload.distance.doubleValue());

        String danger = fastApiClient.predictDanger(data);
        data.setDanger(danger);

        repository.save(data.toEntity());

        String resultJson = "{\"danger\":\"" + danger + "\"}";
        webSocketHandler.sendToAndroidClients(resultJson);

    }
}