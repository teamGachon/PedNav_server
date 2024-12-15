package pednav.backend.pednav.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiResponseDTO {
    private Boolean vehicleDetected; // 차량 소리 감지 여부
}