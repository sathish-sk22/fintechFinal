package com.example.demo.Service;

import com.example.demo.model.OtpCode;
import com.example.demo.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepo;
    private final EmailService emailService;

    @Transactional
    public void generateOtp(String username, String toEmail) {

        otpRepo.findTopByUsernameAndUsedFalse(username)
                .ifPresent(oldOtp -> {
                    oldOtp.setUsed(true);
                    otpRepo.save(oldOtp);
                });

        String code = String.format("%06d", new Random().nextInt(1_000_000));

        OtpCode otpCode = OtpCode.builder()
                .username(username)
                .code(code)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .used(false)
                .build();

        otpRepo.save(otpCode);

        emailService.MailSender(toEmail, "Your OTP Code",
                "Your verification code: " + code + "\nThis code expires in 5 minutes.");

        System.out.println("DEBUG OTP for " + username + " => " + code);
    }

    @Transactional
    public boolean verifyOtp(String username, String code) {

        return otpRepo.findTopByUsernameAndUsedFalse(username)
                .filter(otp -> otp.getCode().equals(code))
                .filter(otp -> otp.getExpiresAt().isAfter(Instant.now()))
                .map(otp -> {
                    otp.setUsed(true);
                    otpRepo.save(otp);
                    return true;
                })
                .orElse(false);
    }
}

