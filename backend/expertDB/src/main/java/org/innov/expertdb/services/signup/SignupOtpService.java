package org.innov.expertdb.services.signup;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.innov.expertdb.auth.dtos.signup.ResendOtpRequest;
import org.innov.expertdb.auth.dtos.signup.VerifyOtpRequest;
import org.innov.expertdb.auth.signup.SignupOtp;
import org.innov.expertdb.repos.SignupOtpRepository;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupOtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_BOUND = 1_000_000;
    private static final String INVALID_OR_EXPIRED_CODE = "Invalid or expired verification code.";

    private final UserRepository userRepository;
    private final SignupOtpRepository signupOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupNotificationService notificationService;

    @Value("${app.signup.otp-expiration-minutes:10}")
    private long otpExpirationMinutes;

    @Transactional
    public void sendVerificationOtp(User user) {
        String otp = String.format("%06d", SECURE_RANDOM.nextInt(OTP_BOUND));
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(otpExpirationMinutes));

        SignupOtp signupOtp = new SignupOtp();
        signupOtp.setEmail(user.getEmail());
        signupOtp.setOtpHash(passwordEncoder.encode(otp));
        signupOtp.setExpiresAt(expiresAt);
        signupOtpRepository.save(signupOtp);

        notificationService.sendVerificationOtp(user, otp, expiresAt);
    }

    @Transactional
    public void resendVerificationOtp(ResendOtpRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .filter(u -> !u.isEnabled())
                .orElseThrow(() -> new RuntimeException(
                        "No pending verification found for this email. Please sign up first."
                ));

        sendVerificationOtp(user);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        String email = normalizeEmail(request.email());
        Instant now = Instant.now();

        SignupOtp signupOtp = signupOtpRepository
                .findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(email, now)
                .orElseThrow(() -> new RuntimeException(INVALID_OR_EXPIRED_CODE));

        if (!passwordEncoder.matches(request.otp().trim(), signupOtp.getOtpHash())) {
            throw new RuntimeException(INVALID_OR_EXPIRED_CODE);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(INVALID_OR_EXPIRED_CODE));

        user.setEnabled(true);
        signupOtp.setUsedAt(now);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
