package pednav.backend.pednav.websocket;

import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.PartialData;
import pednav.backend.pednav.repository.DataRepository;

@Component
public class SensorDataProcessor {

    private final DataRepository repository;

    public SensorDataProcessor(DataRepository repository) {
        this.repository = repository;
    }

    public void process(Long timestamp, SensorDataBuffer.SensorPayload payload) {
        PartialData data = new PartialData(timestamp);
        data.setVehicleDetected(payload.vehicleDetected.doubleValue());
        data.setVelocity(payload.velocity.doubleValue());
        data.setDistance(payload.distance.doubleValue());

        repository.save(data.toEntity());

        System.out.println("✅ 저장됨: " + data.toEntity());
    }
}
