package pednav.backend.pednav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.dto.OffloadingDecisionRequest;
import pednav.backend.pednav.util.OffloadingMetricStore;
import pednav.backend.pednav.websocket.UnifiedWebSocketHandler;

@Service
@RequiredArgsConstructor
public class OffloadingDecisionService {

    private final OffloadingMetricStore metricStore;
    private final UnifiedWebSocketHandler webSocketHandler;

    // 가중치 상수 (게임이론 기반)
    private static final double CPU_WEIGHT = 0.5;
    private static final double BATTERY_WEIGHT = 0.5;
    private static final double LATENCY_WEIGHT = 1.0;

    public void receiveMetric(OffloadingDecisionRequest req) {
        metricStore.save(req);

        if (metricStore.isComplete()) {
            OffloadingDecisionRequest androidMetric = metricStore.getAndroidMetric();
            OffloadingDecisionRequest esp32Metric = metricStore.getEsp32Metric();

            int caseId = determineCase(androidMetric, esp32Metric);

            webSocketHandler.sendToDevice("ANDROID", "{\"case\": " + caseId + "}");
            webSocketHandler.sendToDevice("ESP32", "{\"case\": " + caseId + "}");

            metricStore.clear();
        }
    }

    public int determineCase(OffloadingDecisionRequest android, OffloadingDecisionRequest esp32) {
        boolean androidOffload = shouldOffload(android);
        boolean esp32Offload = shouldOffload(esp32);

        if (!androidOffload && !esp32Offload) return 1;
        else if (androidOffload && !esp32Offload) return 2;
        else if (!androidOffload && esp32Offload) return 3;
        else return 4;
    }

    public boolean shouldOffload(OffloadingDecisionRequest req) {
        double uLocal = calculateLocalUtility(req);
        double uOffload = calculateOffloadUtility(req);
        return uOffload > uLocal;
    }

    private double calculateLocalUtility(OffloadingDecisionRequest req) {
        double cpuScore = 1.0 - req.cpuLoad();      // 낮을수록 유리
        double batteryScore = req.batteryLevel();   // 높을수록 유리
        return CPU_WEIGHT * cpuScore + BATTERY_WEIGHT * batteryScore;
    }

    private double calculateOffloadUtility(OffloadingDecisionRequest req) {
        double latencyScore = 1.0 - (req.latency() / 1000.0); // 낮을수록 유리
        latencyScore = Math.max(0, Math.min(latencyScore, 1.0)); // 0~1 정규화 보장
        return LATENCY_WEIGHT * latencyScore;
    }
}
