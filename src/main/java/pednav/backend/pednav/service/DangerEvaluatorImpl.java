package pednav.backend.pednav.service;

import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.PartialData;

@Component
public class DangerEvaluatorImpl implements DangerEvaluator {
    @Override
    public String evaluateDanger(PartialData data) {
        double vehicleDetected = data.getVehicleDetected();
        double distance = data.getDistance();
        double velocity = data.getVelocity();

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
