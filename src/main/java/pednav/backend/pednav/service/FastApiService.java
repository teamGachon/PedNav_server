package pednav.backend.pednav.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pednav.backend.pednav.dto.FastApiResponseDTO;

@Service
public class FastApiService {

    private final String fastApiUrl = "http://<FASTAPI_SERVER_IP>:8000/predict"; // FastAPI 엔드포인트

    public Boolean callFastApi(String filePath) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FastApiResponseDTO> response =
                restTemplate.postForEntity(fastApiUrl, filePath, FastApiResponseDTO.class);

        if (response.getBody() != null) {
            return response.getBody().getVehicleDetected(); // 차량 감지 여부 반환
        } else {
            throw new RuntimeException("FastAPI에서 결과를 반환하지 않았습니다.");
        }
    }
}
