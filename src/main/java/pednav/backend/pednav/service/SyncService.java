package pednav.backend.pednav.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.WebSocketSession;
import pednav.backend.pednav.dto.PartialData;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.websocket.UnifiedWebSocketHandler;
import org.json.JSONObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SyncService {

    private UnifiedWebSocketHandler webSocketHandler;
    private final DataRepository repository;

    private final Map<Long, PartialData> buffer = new ConcurrentHashMap<>();

    public SyncService(DataRepository repository, UnifiedWebSocketHandler webSocketHandler) {
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
            data.setFrequency(obj.getDouble("frequency"));
            data.setDistance(obj.getDouble("distance"));
        }

        buffer.put(timestamp, data);

        if (data.isComplete()) {
            repository.save(data.toEntity());

            // FastAPI 요청 (비동기 가능)
            String result = sendToFastAPI(data);

            // 결과를 Android로 전송
            webSocketHandler.sendToAndroidClients(result);

            buffer.remove(timestamp);
        }
    }

    private String sendToFastAPI(PartialData data) {
        try {
            WebClient client = WebClient.create("http://your-fastapi-url.com");
            String response = client.post()
                    .uri("/predict")
                    .bodyValue(data)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 또는 async로 변경 가능
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"danger\":false}";
        }
    }
}
