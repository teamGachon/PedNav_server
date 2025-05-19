package pednav.backend.pednav.dto;

public record Case2DangerRequest(
        Long timestamp,
        Double radarDetected,   // ESP32 local 연산 결과
        byte[] audioPcm         // Android에서 보낸 raw PCM 오디오
) {}
