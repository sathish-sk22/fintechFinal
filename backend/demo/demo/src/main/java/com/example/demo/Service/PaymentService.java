package com.example.demo.Service;

import com.example.demo.Dto.PaymentRequestDto;
import com.example.demo.Dto.PaymentResponseDto;
import com.example.demo.Entity.FraudResult;
import com.example.demo.Entity.KycStatus;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.TransactionStatus;
import com.example.demo.Util.UserDetails;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FraudDetectionService fraudDetectionService;
    private final UserRepository userRepository;
    private final KycService kycService;
    private final OtpService otpService;


    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentResponseDto processPayment(
            String senderUsername,
            PaymentRequestDto request
    ) {

        // 1️⃣ Validate sender
        UserDetails sender = userRepository.findByUsername(senderUsername)
                .orElse(null);

        if (sender == null) {
            return failed("Sender not found");
        }

        // 1.5️⃣ KYC CHECK (🔥 IMPORTANT)
        KycStatus kycStatus = kycService.getKycStatus(senderUsername);
        if (kycStatus != KycStatus.APPROVED) {
            return failed("KYC not approved. Please complete KYC.");
        }

        // 2️⃣ Validate receiver
        UserDetails receiver = userRepository.findById(request.getToUserId())
                .orElse(null);

        if (receiver == null) {
            return failed("Receiver not found");
        }

        // 3️⃣ Validate amount
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return failed("Invalid amount");
        }

        // 4️⃣ Fraud check
        FraudResult fraudResult =
                fraudDetectionService.checkFraud(senderUsername, amount);

        boolean highRisk = fraudResult == FraudResult.HIGH;

        Transaction transaction = Transaction.builder()
                .fromUser(sender.getUsername())
                .toUser(receiver.getUsername())
                .amount(amount)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .status(
                        highRisk
                                ? TransactionStatus.PENDING_OTP
                                : TransactionStatus.SUCCESS
                )
                .build();

        transactionRepository.save(transaction);

        // 5️⃣ OTP flow for high-risk
        if (highRisk) {
            otpService.generateOtp(sender.getUsername(), sender.getEmail());

            return PaymentResponseDto.builder()
                    .transactionId(transaction.getId())
                    .status("OTP_REQUIRED")
                    .message("High-risk payment. OTP sent for verification.")
                    .build();
        }

        // 6️⃣ Normal success
        return PaymentResponseDto.builder()
                .transactionId(transaction.getId())
                .status("SUCCESS")
                .amount(transaction.getAmount())
                .message("Payment completed successfully")
                .timestamp(transaction.getCreatedAt())
                .build();
    }

    private PaymentResponseDto failed(String message) {
        return PaymentResponseDto.builder()
                .status("FAILED")
                .message(message)
                .build();
    }

}
