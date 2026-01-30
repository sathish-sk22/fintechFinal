package com.example.demo.Service;

import com.example.demo.Entity.FraudResult;
import com.example.demo.Entity.RiskLog;
import com.example.demo.repository.RiskLogRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final RiskLogRepository riskLogRepository;

    private static final BigDecimal HIGH_AMOUNT = new BigDecimal("10000");

    public FraudResult checkFraud(String username, BigDecimal amount) {

        boolean highRisk = false;

        // Rule 1: High amount
        if (amount.compareTo(HIGH_AMOUNT) >= 0) {
            saveRisk(username, "HIGH_AMOUNT", "HIGH",
                    "Transaction amount exceeds threshold");
            highRisk = true;
        }

        // Rule 2: Velocity
        long recentTxCount =
                transactionRepository.countByFromUserAndCreatedAtAfter(
                        username,
                        LocalDateTime.now().minusMinutes(1)
                );

        if (recentTxCount >= 3) {
            saveRisk(username, "VELOCITY", "HIGH",
                    "Multiple transactions in short time");
            highRisk = true;
        }

        // Rule 3: First transaction large
        long totalTx = transactionRepository.countByFromUser(username);
        if (totalTx == 0 && amount.compareTo(new BigDecimal("2000")) >= 0) {
            saveRisk(username, "FIRST_TIME", "MEDIUM",
                    "Large first transaction");
        }

        return highRisk ? FraudResult.HIGH : FraudResult.LOW;
    }

    private void saveRisk(String username, String type, String severity, String desc) {
        riskLogRepository.save(
                RiskLog.builder()
                        .username(username)
                        .riskType(type)
                        .severity(severity)
                        .description(desc)
                        .detectedAt(LocalDateTime.now())
                        .build()
        );
    }
}