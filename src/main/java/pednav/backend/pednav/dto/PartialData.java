package pednav.backend.pednav.dto;

import pednav.backend.pednav.domain.entity.DetectionResult;

public class PartialData {
    private Long timestamp;
    private Double vehicleDetected;
    private Double velocity, distance;
    public Long getTimestamp() { return timestamp; }
    public Double getVehicleDetected() { return vehicleDetected; }
    public Double getVelocity() { return velocity; }
    public Double getDistance() { return distance; }


    public PartialData(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setVehicleDetected(Double val) { this.vehicleDetected = val; }
    public void setVelocity(Double v) { this.velocity = v; }
    public void setDistance(Double d) { this.distance = d; }

    public boolean isComplete() {
        return vehicleDetected != null && velocity != null && distance != null;
    }

    public DetectionResult toEntity() {
        DetectionResult entity = new DetectionResult();
        entity.setTimestamp(timestamp);
        entity.setVehicleDetected(vehicleDetected);
        entity.setVelocity(velocity);
        entity.setDistance(distance);
        return entity;
    }
}

