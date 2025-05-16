package pednav.backend.pednav.websocket;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SensorDataBuffer {

    public static class SensorPayload {
        public Float vehicleDetected;  // from Android
        public Float velocity, distance;  // from ESP32
    }

    private final ConcurrentHashMap<Long, SensorPayload> buffer = new ConcurrentHashMap<>();
    private final SensorDataProcessor processor;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long SYNC_WINDOW_MS = 400;  // ±400ms 허용

    public SensorDataBuffer(SensorDataProcessor processor) {
        this.processor = processor;
    }

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::flush, 0, 1, TimeUnit.SECONDS);
    }

    public void putAndroidData(long timestamp, float vehicleDetected) {
        buffer.compute(timestamp, (ts, existing) -> {
            if (existing == null) existing = new SensorPayload();
            existing.vehicleDetected = vehicleDetected;
            return existing;
        });
    }

    public void putESP32Data(long timestamp, float velocity, float distance) {
        buffer.compute(timestamp, (ts, existing) -> {
            if (existing == null) existing = new SensorPayload();
            existing.velocity = velocity;
            existing.distance = distance;
            return existing;
        });
    }

    private void flush() {
        var timestamps = new ArrayList<>(buffer.keySet());
        timestamps.sort(Long::compareTo);

        Set<Long> matched = new HashSet<>();

        for (long ts1 : timestamps) {
            if (matched.contains(ts1)) continue;
            SensorPayload p1 = buffer.get(ts1);
            if (p1 == null) continue;

            for (long ts2 : timestamps) {
                if (ts1 == ts2 || matched.contains(ts2)) continue;

                long delta = Math.abs(ts1 - ts2);
                if (delta > SYNC_WINDOW_MS) continue;

                SensorPayload p2 = buffer.get(ts2);
                if (p2 == null) continue;

                boolean c1 = p1.vehicleDetected != null && p2.velocity != null && p2.distance != null;
                boolean c2 = p2.vehicleDetected != null && p1.velocity != null && p1.distance != null;


                if (c1 || c2) {
                    SensorPayload merged = new SensorPayload();
                    merged.vehicleDetected = (p1.vehicleDetected != null) ? p1.vehicleDetected : p2.vehicleDetected;
                    merged.velocity = (p1.velocity != null) ? p1.velocity : p2.velocity;
                    merged.distance = (p1.distance != null) ? p1.distance : p2.distance;

                    long mergedTs = Math.min(ts1, ts2);


                    processor.process(mergedTs, merged);

                    matched.add(ts1);
                    matched.add(ts2);
                    buffer.remove(ts1);
                    buffer.remove(ts2);
                    break;
                }
            }
        }
    }
}
