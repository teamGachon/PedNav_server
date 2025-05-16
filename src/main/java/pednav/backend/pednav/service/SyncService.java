package pednav.backend.pednav.service;

import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.websocket.SensorDataBuffer;

@Service
// ✅ Setter 제거 및 생성자에서도 webSocketHandler 제거
public class SyncService {

    private final DataRepository repository;
    private final SensorDataBuffer buffer;

    public SyncService(DataRepository repository, @Lazy SensorDataBuffer buffer) {
        this.repository = repository;
        this.buffer = buffer;
    }

    public void processIncomingJson(String json) {
        try {
            System.out.println("📥 수신된 JSON: " + json);
            JSONObject obj = new JSONObject(json);
            long timestamp = obj.getLong("timestamp");

            if (obj.has("vehicle_detected")) {
                float val = (float) obj.getDouble("vehicle_detected");
                buffer.putAndroidData(timestamp, val);
            }
            if (obj.has("velocity") && obj.has("distance")) {
                float vel = (float) obj.getDouble("velocity");
                float dist = (float) obj.getDouble("distance");
                buffer.putESP32Data(timestamp, vel, dist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
