package com.example.demo.Controller;

import com.example.demo.repository.RiskLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskLogRepository riskLogRepository;

    @GetMapping("/logs")
    public ResponseEntity<?> getAllRisks() {
        return ResponseEntity.ok(riskLogRepository.findAll());
    }
}
