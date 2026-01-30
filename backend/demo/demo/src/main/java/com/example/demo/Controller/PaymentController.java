package com.example.demo.Controller;

import com.example.demo.Dto.PaymentRequestDto;
import com.example.demo.Dto.PaymentResponseDto;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.TransactionStatus;
import com.example.demo.Service.OtpService;
import com.example.demo.Service.PaymentService;
import com.example.demo.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OtpService otpService;
    private final TransactionRepository transactionRepository;

    /**
     * Send money from logged-in user to another user
     */
    @PostMapping("/send")
    public ResponseEntity<PaymentResponseDto> sendPayment(
            @Valid @RequestBody PaymentRequestDto request,
            Authentication authentication
    ) {
        // sender username from JWT
        String senderUsername = authentication.getName();

        PaymentResponseDto response =
                paymentService.processPayment(senderUsername, request);

        if ("FAILED".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Health check for hackathon demo
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment service is running");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyPaymentOtp(
            @RequestParam Long transactionId,
            @RequestParam String code,
            Authentication authentication
    ) {
        String username = authentication.getName();

        boolean verified = otpService.verifyOtp(username, code);
        if (!verified) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow();

        // 🔐 Ownership check
        if (!tx.getFromUser().equals(username)) {
            return ResponseEntity.status(403).body("Unauthorized transaction access");
        }

        // 🔐 State check
        if (!tx.getStatus().equals(TransactionStatus.PENDING_OTP)) {
            return ResponseEntity.badRequest().body("Transaction not awaiting OTP");
        }

        tx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(tx);

        return ResponseEntity.ok(
                PaymentResponseDto.builder()
                        .transactionId(tx.getId())
                        .status("SUCCESS")
                        .message("Payment completed after OTP verification")
                        .build()
        );
    }
}

