package com.example.demo.Controller;

import com.example.demo.Dto.LoginRequest;
import com.example.demo.Dto.LoginResponse;
import com.example.demo.Dto.SignUpReq;
import com.example.demo.Security.JwtUtil;
import com.example.demo.Service.UserService;
import com.example.demo.Service.OtpService;
import com.example.demo.Util.UserDetails;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    // 🔹 SIGNUP (password-based)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpReq request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    // 🔹 LOGIN (password-based)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);

        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // 🔹 REQUEST OTP (new OR existing user)
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody SignUpReq request) {

        userService.createUserIfNotExists(request);
        otpService.generateOtp(request.getUsername(), request.getEmail());

        return ResponseEntity.ok("OTP sent successfully");
    }

    // 🔹 VERIFY OTP + LOGIN
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String username,
            @RequestParam String code
    ) {

        boolean valid = otpService.verifyOtp(username, code);
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        return ResponseEntity.ok(
                userService.loginAfterOtp(username)
        );
    }
}

