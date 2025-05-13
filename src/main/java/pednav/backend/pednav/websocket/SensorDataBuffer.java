package pednav.backend.pednav.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorDataBuffer {

    public static class SensorPayload {
        public Float vehicleDetected;  // from Android
        public Float velocity, distance;  // from ESP32
    }

    private final ConcurrentHashMap<Long, SensorPayload> buffer = new ConcurrentHashMap<>();
    private final SensorDataProcessor processor;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SensorDataBuffer(SensorDataProcessor processor) {
        this.processor = processor;
        scheduler.scheduleAtFixedRate(this::flush, 0, 1, TimeUnit.SECONDS);
    }

    public void putAndroidData(long timestamp, float vehicleDetected) {
        buffer.compute(timestamp, (ts, existing) -> {
            if (existing == null) existing = new SensorPayload();
            existing.vehicleDetected = vehicleDetected;
            return existing;
        });
    }

    public void putESP32Data(long timestamp, float velocity,  float distance) {
        buffer.compute(timestamp, (ts, existing) -> {
            if (existing == null) existing = new SensorPayload();
            existing.velocity = velocity;
            existing.distance = distance;
            return existing;
        });
    }

    // 매 1초마다 실행: 동기화된 항목을 처리
    private void flush() {
        buffer.forEach((timestamp, payload) -> {
            if (payload.vehicleDetected != null &&
                    payload.velocity != null && payload.distance != null) {

                processor.process(timestamp, payload);
                buffer.remove(timestamp);
            }
        });
    }
}

