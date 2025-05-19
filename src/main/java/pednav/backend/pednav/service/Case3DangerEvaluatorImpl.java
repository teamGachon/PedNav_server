package pednav.backend.pednav.service;

import org.springframework.stereotype.Component;
import pednav.backend.pednav.dto.Case3DangerRequest;

@Component
public class Case3DangerEvaluatorImpl implements Case3DangerEvaluator {
    @Override
    public String evaluateDanger(Case3DangerRequest data) {
        double soundDetected = data.getSoundDetected();
        double distance = data.getDistance();
        double velocity = data.getVelocity();

        boolean isApproaching = velocity >= 0;

        if (soundDetected >= 0.4) {
            if (isApproaching) {
                if (distance < 5 && velocity > 20) return "HIGH";
                else if (distance < 10 && velocity > 10) return "MEDIUM";
                else return "LOW";
            } else {
                if (distance < 3 && velocity < -20) return "MEDIUM";
                else return "LOW";
            }
        } else {
            if (isApproaching) {
                if (distance < 2 && velocity > 10) return "HIGH";
                else if (distance < 4 && velocity > 5) return "MEDIUM";
                else return "LOW";
            } else {
                if (distance < 1 && velocity < -10) return "MEDIUM";
                else return "LOW";
            }
        }
    }
}
