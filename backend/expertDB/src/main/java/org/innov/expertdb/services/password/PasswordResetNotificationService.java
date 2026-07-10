package org.innov.expertdb.services.password;

import java.time.Instant;

import org.innov.expertdb.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetNotificationService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String mailFrom;

    public PasswordResetNotificationService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.password-reset.mail-from:no-reply@localhost}") String mailFrom
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailFrom = mailFrom;
    }

    public void sendPasswordResetOtp(User user, String otp, Instant expiresAt) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn(
                    "Password reset OTP for {} is {}. It expires at {}. Configure SMTP to send reset emails.",
                    user.getEmail(),
                    otp,
                    expiresAt
            );
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("University of Zimbabwe researcher portal password reset");
        message.setText("Your password reset code is " + otp + ". It expires at " + expiresAt + ".");

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.warn("Could not send password reset email to {}: {}", user.getEmail(), exception.getMessage());
        }
    }
}