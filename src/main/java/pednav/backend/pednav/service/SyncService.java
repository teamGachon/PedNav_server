package pednav.backend.pednav.service;

import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.repository.DataRepository;
import pednav.backend.pednav.websocket.Case2SensorBuffer;
import pednav.backend.pednav.websocket.Case3SensorBuffer;

@Service
public class SyncService {

    private final DataRepository repository;
    private final Case2SensorBuffer case2Buffer;
    private final Case3SensorBuffer case3Buffer;

    public SyncService(
            DataRepository repository,
            @Lazy Case2SensorBuffer case2Buffer,
            @Lazy Case3SensorBuffer case3Buffer
    ) {
        this.repository = repository;
        this.case2Buffer = case2Buffer;
        this.case3Buffer = case3Buffer;
    }

    public void processIncomingJson(String json) {
        try {
            System.out.println("📥 수신된 JSON: " + json);
            JSONObject obj = new JSONObject(json);
            long timestamp = obj.getLong("timestamp");

            // ✅ vehicle_detected: Case2
            if (obj.has("vehicle_detected")) {
                float val = (float) obj.getDouble("vehicle_detected");
                case2Buffer.putRadarData(timestamp, val);
                case3Buffer.putAndroidData(timestamp, val); // Case3에도 사용됨
            }

            // ✅ velocity + distance: Case3
            if (obj.has("velocity") && obj.has("distance")) {
                float vel = (float) obj.getDouble("velocity");
                float dist = (float) obj.getDouble("distance");
                case3Buffer.putESP32Data(timestamp, vel, dist);
            }

            // ✅ audio_pcm: Case2 Android 업로드용 (Base64 encoded가 아니라면 수정 필요)
            // 지금은 JSON에 포함된 PCM audio는 Android에서 직접 REST로 보내기 때문에 생략 가능

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
