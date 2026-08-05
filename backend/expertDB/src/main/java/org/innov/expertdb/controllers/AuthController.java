package org.innov.expertdb.controllers;
import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.auth.dtos.password.PasswordResetConfirmRequest;
import org.innov.expertdb.auth.dtos.password.PasswordResetRequest;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.auth.dtos.signup.ResendOtpRequest;
import org.innov.expertdb.auth.dtos.signup.VerifyOtpRequest;
import org.innov.expertdb.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signUp(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

     @PostMapping("/login")
     public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
         LoginResponse response = authService.login(request);
         return ResponseEntity.ok(response);
     }

     @PostMapping("/password-reset/request")
     public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
         authService.requestPasswordReset(request);
         return ResponseEntity.ok("If an account exists for that email, a reset code has been sent.");
     }

     @PostMapping("/password-reset/confirm")
     public ResponseEntity<String> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
         authService.confirmPasswordReset(request);
         return ResponseEntity.ok("Password reset successful. You can now log in.");
     }

     @PostMapping("/verify-otp")
     public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
         authService.verifySignupOtp(request);
         return ResponseEntity.ok("Email verified. Your registration is now pending admin approval.");
     }

     @PostMapping("/resend-otp")
     public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
         authService.resendSignupOtp(request);
         return ResponseEntity.ok("A new verification code has been sent to your email.");
     }
 }