package pednav.backend.pednav.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pednav.backend.pednav.dto.PartialData;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.websocket.UnifiedWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SyncService {

    private UnifiedWebSocketHandler webSocketHandler;
    private final DataRepository repository;

    private final Map<Long, PartialData> buffer = new ConcurrentHashMap<>();

    public SyncService(DataRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setWebSocketHandler(UnifiedWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void processIncomingJson(String json, WebSocketSession session) throws Exception {
        JSONObject obj = new JSONObject(json);
        long timestamp = obj.getLong("timestamp");

        PartialData data = buffer.getOrDefault(timestamp, new PartialData(timestamp));

        if (obj.has("vehicle_detected")) {
            data.setVehicleDetected(obj.getDouble("vehicle_detected"));
        }
        if (obj.has("velocity")) {
            data.setVelocity(obj.getDouble("velocity"));
            data.setDistance(obj.getDouble("distance"));
        }

        buffer.put(timestamp, data);

        if (data.isComplete()) {
            repository.save(data.toEntity());

            // danger 판단 로직 실행
            String danger = evaluateDanger(data);

            // 로그 출력
            System.out.printf("🚨 Danger 평가: %s [timestamp=%d, vehicle_detected=%.2f, distance=%.2f m, velocity=%.2f km/h]%n",
                    danger, data.getTimestamp(), data.getVehicleDetected(), data.getDistance(), data.getVelocity());

            // Android로 전송 (JSON 형태)
            String resultJson = "{\"danger\":\"" + danger + "\"}";
            webSocketHandler.sendToAndroidClients(resultJson);

            buffer.remove(timestamp);
        }
    }

    // 🚨 내부 판단 알고리즘
    private String evaluateDanger(PartialData data) {
        double vehicleDetected = data.getVehicleDetected();
        double distance = data.getDistance(); // meters
        double velocity = data.getVelocity(); // km/h

        if (vehicleDetected >= 0.4) {
            if (distance < 5 && velocity > 20) return "HIGH";
            else if (distance < 10 && velocity > 10) return "MEDIUM";
            else return "LOW";
        } else {
            if (distance < 2 && velocity < 10) return "HIGH";
            else if (distance < 4 && velocity < 15) return "MEDIUM";
            else return "LOW";
        }
    }
}
