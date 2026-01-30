package com.example.demo.Controller;

import com.example.demo.Dto.KycRequestDto;
import com.example.demo.Dto.KycResponseDto;
import com.example.demo.Service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/submit")
    public ResponseEntity<KycResponseDto> submitKyc(
            @Valid @RequestBody KycRequestDto request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                kycService.submitKyc(authentication.getName(), request)
        );
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(Authentication authentication) {
        return ResponseEntity.ok(
                kycService.getKycStatus(authentication.getName())
        );
    }
}
