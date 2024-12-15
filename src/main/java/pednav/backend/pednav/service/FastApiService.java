package pednav.backend.pednav.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pednav.backend.pednav.dto.FastApiResponseDTO;

@Service
public class FastApiService {

    private final String fastApiUrl = "http://172.25.82.102:8000/predict"; // FastAPI 엔드포인트

    public FastApiResponseDTO callFastApi(String filePath) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FastApiResponseDTO> response =
                restTemplate.postForEntity(fastApiUrl, filePath, FastApiResponseDTO.class);

        if (response.getBody() != null) {
            return response.getBody(); // vehicleDetected와 result 값을 함께 반환
        } else {
            throw new RuntimeException("FastAPI에서 결과를 반환하지 않았습니다.");
        }
    }
}
