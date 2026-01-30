package com.example.demo.Service;

import com.example.demo.Dto.LoginRequest;
import com.example.demo.Dto.LoginResponse;
import com.example.demo.Dto.SignUpReq;
import com.example.demo.Security.JwtUtil;
import com.example.demo.Util.UserDetails;
import com.example.demo.repository.OtpRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 🔹 SIGNUP (password-based)
    public String signup(SignUpReq request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        UserDetails user = UserDetails.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        request.getPassword() == null
                                ? null
                                : passwordEncoder.encode(request.getPassword())
                )
                .enabled(false) // enabled after OTP
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    // 🔹 LOGIN (password-based)
    public LoginResponse login(LoginRequest request) {

        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new LoginResponse(null, "User not found");
        }

        if (user.getPassword() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(null, "Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token, "Login successful");
    }

    // 🔹 OTP login (after verification)
    public LoginResponse loginAfterOtp(String username) {

        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        String token = jwtUtil.generateToken(username);
        return new LoginResponse(token, "OTP verified & login successful");
    }

    // 🔹 Used by OTP request
    public void createUserIfNotExists(SignUpReq request) {

        userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    UserDetails user = UserDetails.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .enabled(false)
                            .build();
                    return userRepository.save(user);
                });
    }
}

