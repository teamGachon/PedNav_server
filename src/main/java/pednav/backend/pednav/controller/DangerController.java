package pednav.backend.pednav.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pednav.backend.pednav.dto.*;
import pednav.backend.pednav.service.*;

@RestController
@RequestMapping("/api/danger")
@RequiredArgsConstructor
public class DangerController {

    private final Case1DangerService case1Service;
    private final Case2DangerService case2Service;
    private final Case3DangerService case3Service;

    @PostMapping("/case1")
    public ResponseEntity<String> handleCase1(@RequestBody Case1DangerRequest request) {
        String danger = case1Service.evaluateDanger(request);
        return ResponseEntity.ok(danger);
    }

    @PostMapping("/case2")
    public ResponseEntity<String> handleCase2(
            @RequestParam("radar_detected") Double radarDetected,
            @RequestPart("audio_file") MultipartFile audioFile
    ) {
        try {
            long timestamp = System.currentTimeMillis();
            String danger = case2Service.evaluateAndSaveDanger(timestamp, radarDetected, audioFile.getBytes());
            return ResponseEntity.ok(danger);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }

    @PostMapping("/case3")
    public ResponseEntity<String> handleCase3(@RequestBody Case3DangerRequest request) {
        String danger = case3Service.evaluateAndSaveDanger(request);
        return ResponseEntity.ok(danger);
    }

    // Case4는 추후 확장
}
