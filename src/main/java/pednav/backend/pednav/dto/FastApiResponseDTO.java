package pednav.backend.pednav.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiResponseDTO {
    private Boolean vehicleDetected; // 차량 소리 감지 여부
    private Double result; // 예측값 (0~1)

    @Override
    public String toString() {
        return "FastApiResponseDTO{" +
                "vehicleDetected=" + vehicleDetected +
                ", result=" + result +
                '}';
    }
}