package pednav.backend.pednav.service;

import org.springframework.stereotype.Service;
import pednav.backend.pednav.dto.OffloadingDecisionRequest;

@Service
public class OffloadingDecisionService {

    public boolean shouldOffload(OffloadingDecisionRequest req) {
        // 유틸리티 계산
        double U_local = localUtility(req);
        double U_offload = offloadUtility(req);
        return U_offload > U_local;
    }

    private double localUtility(OffloadingDecisionRequest req) {
        return (1 - req.cpuLoad()) * 0.5 + req.batteryLevel() * 0.5;
    }

    private double offloadUtility(OffloadingDecisionRequest req) {
        double dangerWeight = Math.min(req.vehicleDetected() + Math.abs(req.velocity()) + (1 / (req.distance() + 0.1)), 3);
        return (1 - req.latency() / 1000) * 0.5 + (dangerWeight / 3.0) * 0.5;
    }
}