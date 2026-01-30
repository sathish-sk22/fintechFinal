package com.example.demo.Service;

import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service

@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Transactional
    public void register(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        // If user already exists
        if (user != null) {

            // If already verified → treat as login
            if (user.isVerified()) {
                sendOtp(email);
                return;
            }

            // If not verified → resend OTP
            sendOtp(email);
            return;
        }

        // New user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setVerified(false);
        userRepository.save(newUser);

        sendOtp(email);
    }

    // LOGIN (send OTP)
    public void login(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not registered"));

        sendOtp(email);
    }

    // SEND OTP (internal)
    private void sendOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpEntity entity = new OtpEntity();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.deleteByEmail(email);
        otpRepository.save(entity);

        emailService.sendOtp(email, otp);
    }

    // VERIFY OTP
    public void verifyOtp(String email, String otp) {
        OtpEntity entity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (entity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!entity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        otpRepository.deleteByEmail(email);
    }
}
