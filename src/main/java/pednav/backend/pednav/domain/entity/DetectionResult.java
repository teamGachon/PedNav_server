package pednav.backend.pednav.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DetectionResult {
    @Id
    @GeneratedValue
    private Long id;

    private Long timestamp;
    private Double vehicleDetected;
    private Double velocity;
    private Double frequency;
    private Double distance;
}
