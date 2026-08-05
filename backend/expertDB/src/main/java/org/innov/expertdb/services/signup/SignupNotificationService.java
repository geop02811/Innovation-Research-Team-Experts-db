package org.innov.expertdb.services.signup;

import java.time.Instant;

import org.innov.expertdb.services.email.EmailService;
import org.innov.expertdb.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignupNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SignupNotificationService.class);

    private final EmailService emailService;
    private final String mailFrom;

    public SignupNotificationService(
            EmailService emailService,
            @Value("${app.signup.mail-from:no-reply@localhost}") String mailFrom
    ) {
        this.emailService = emailService;
        this.mailFrom = mailFrom;
    }

    public void sendVerificationOtp(User user, String otp, Instant expiresAt) {
        if (!emailService.isConfigured()) {
            log.warn(
                    "Signup verification OTP for {} is {}. It expires at {}. Configure SMTP to send verification emails.",
                    user.getEmail(),
                    otp,
                    expiresAt
            );
            return;
        }

        emailService.send(
                mailFrom,
                user.getEmail(),
                "Verify your UEDMS account",
                "Your University of Zimbabwe account verification code is " + otp
                        + ". It expires at " + expiresAt + "."
        );
    }
}
