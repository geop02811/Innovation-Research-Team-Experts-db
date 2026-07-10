package org.innov.expertdb.services.password;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.innov.expertdb.auth.dtos.password.PasswordResetConfirmRequest;
import org.innov.expertdb.auth.dtos.password.PasswordResetRequest;
import org.innov.expertdb.auth.password.PasswordResetOtp;
import org.innov.expertdb.repos.PasswordResetOtpRepository;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_BOUND = 1_000_000;
    private static final String INVALID_OR_EXPIRED_CODE = "Invalid or expired reset code.";

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetNotificationService notificationService;

    @Value("${app.password-reset.otp-expiration-minutes:10}")
    private long otpExpirationMinutes;

    @Transactional
    public void requestReset(PasswordResetRequest request) {
        String email = normalizeEmail(request.email());
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return;
        }

        String otp = String.format("%06d", SECURE_RANDOM.nextInt(OTP_BOUND));
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(otpExpirationMinutes));

        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setEmail(email);
        passwordResetOtp.setOtpHash(passwordEncoder.encode(otp));
        passwordResetOtp.setExpiresAt(expiresAt);
        passwordResetOtpRepository.save(passwordResetOtp);

        notificationService.sendPasswordResetOtp(user.get(), otp, expiresAt);
    }

    @Transactional
    public void confirmReset(PasswordResetConfirmRequest request) {
        String email = normalizeEmail(request.email());
        Instant now = Instant.now();

        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository
                .findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(email, now)
                .orElseThrow(() -> new RuntimeException(INVALID_OR_EXPIRED_CODE));

        if (!passwordEncoder.matches(request.otp().trim(), passwordResetOtp.getOtpHash())) {
            throw new RuntimeException(INVALID_OR_EXPIRED_CODE);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(INVALID_OR_EXPIRED_CODE));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        passwordResetOtp.setUsedAt(now);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}