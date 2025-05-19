package pednav.backend.pednav.util;

import lombok.Getter;
import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.OffloadingDecisionRequest;

@Component
public class OffloadingMetricStore {

    @Getter
    private OffloadingDecisionRequest androidMetric;

    @Getter
    private OffloadingDecisionRequest esp32Metric;

    public void save(OffloadingDecisionRequest req) {
        if ("ANDROID".equalsIgnoreCase(req.deviceType())) {
            this.androidMetric = req;
        } else if ("ESP32".equalsIgnoreCase(req.deviceType())) {
            this.esp32Metric = req;
        }
    }

    public boolean isComplete() {
        return androidMetric != null && esp32Metric != null;
    }

    public void clear() {
        this.androidMetric = null;
        this.esp32Metric = null;
    }
}
