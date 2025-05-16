package pednav.backend.pednav.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pednav.backend.pednav.dto.PartialData;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String FAST_API_URL = "http://3.39.233.144:8000/predict"; // 👈 FastAPI 주소

    public String predictDanger(PartialData data) {
        try {
            JsonNode request = objectMapper.createObjectNode()
                    .put("vehicle_detected", data.getVehicleDetected())
                    .put("velocity", data.getVelocity())
                    .put("distance", data.getDistance());

            String response = restTemplate.postForObject(FAST_API_URL, request, String.class);
            JsonNode json = objectMapper.readTree(response);
            return json.get("danger").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
    }
}
