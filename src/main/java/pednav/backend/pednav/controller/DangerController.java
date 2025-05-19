package pednav.backend.pednav.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pednav.backend.pednav.dto.*;
import pednav.backend.pednav.service.*;

@RestController
@RequestMapping("/api/danger")
@RequiredArgsConstructor
public class DangerController {

    private final Case1DangerService case1Service;
    private final Case3DangerService case3Service;

    @PostMapping("/case1")
    public ResponseEntity<String> handleCase1(@RequestBody Case1DangerRequest request) {
        String danger = case1Service.evaluateDanger(request);
        return ResponseEntity.ok(danger);
    }

    @PostMapping("/case3")
    public ResponseEntity<String> handleCase3(@RequestBody Case3DangerRequest request) {
        String danger = case3Service.evaluateDanger(request);
        return ResponseEntity.ok(danger);
    }

    // Case2, Case4는 이후 확장
}
