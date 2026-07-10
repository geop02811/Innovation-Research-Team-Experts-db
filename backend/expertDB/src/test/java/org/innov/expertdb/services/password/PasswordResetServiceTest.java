package org.innov.expertdb.services.password;

import java.time.Instant;
import java.util.Optional;

import org.innov.expertdb.auth.dtos.password.PasswordResetConfirmRequest;
import org.innov.expertdb.auth.dtos.password.PasswordResetRequest;
import org.innov.expertdb.auth.password.PasswordResetOtp;
import org.innov.expertdb.repos.PasswordResetOtpRepository;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetOtpRepository passwordResetOtpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetNotificationService notificationService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "otpExpirationMinutes", 10L);
        user = new User();
        user.setEmail("person@students.uz.ac.zw");
        user.setPasswordHash("old-hash");
        user.setTokenVersion(2);
    }

    @Test
    void requestReset_WithExistingEmail_ShouldSaveHashedOtpAndNotifyUser() {
        when(userRepository.findByEmail("person@students.uz.ac.zw")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("otp-hash");

        passwordResetService.requestReset(new PasswordResetRequest("PERSON@STUDENTS.UZ.AC.ZW"));

        ArgumentCaptor<PasswordResetOtp> otpCaptor = ArgumentCaptor.forClass(PasswordResetOtp.class);
        verify(passwordResetOtpRepository).save(otpCaptor.capture());
        PasswordResetOtp savedOtp = otpCaptor.getValue();

        assertEquals("person@students.uz.ac.zw", savedOtp.getEmail());
        assertEquals("otp-hash", savedOtp.getOtpHash());
        assertNotNull(savedOtp.getExpiresAt());
        verify(notificationService).sendPasswordResetOtp(eq(user), any(), eq(savedOtp.getExpiresAt()));
    }

    @Test
    void requestReset_WithUnknownEmail_ShouldNotSaveOrNotify() {
        when(userRepository.findByEmail("missing@students.uz.ac.zw")).thenReturn(Optional.empty());

        passwordResetService.requestReset(new PasswordResetRequest("missing@students.uz.ac.zw"));

        verify(passwordResetOtpRepository, never()).save(any());
        verify(notificationService, never()).sendPasswordResetOtp(any(), any(), any());
    }

    @Test
    void confirmReset_WithValidOtp_ShouldUpdatePasswordAndInvalidateTokens() {
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setEmail("person@students.uz.ac.zw");
        passwordResetOtp.setOtpHash("otp-hash");
        passwordResetOtp.setExpiresAt(Instant.now().plusSeconds(600));

        when(passwordResetOtpRepository.findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("person@students.uz.ac.zw"), any()
        )).thenReturn(Optional.of(passwordResetOtp));
        when(passwordEncoder.matches("123456", "otp-hash")).thenReturn(true);
        when(userRepository.findByEmail("person@students.uz.ac.zw")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newSecret123")).thenReturn("new-hash");

        passwordResetService.confirmReset(new PasswordResetConfirmRequest(
                "PERSON@STUDENTS.UZ.AC.ZW",
                "123456",
                "newSecret123"
        ));

        assertEquals("new-hash", user.getPasswordHash());
        assertEquals(3, user.getTokenVersion());
        assertNotNull(passwordResetOtp.getUsedAt());
    }

    @Test
    void confirmReset_WithInvalidOtp_ShouldThrow() {
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setEmail("person@students.uz.ac.zw");
        passwordResetOtp.setOtpHash("otp-hash");
        passwordResetOtp.setExpiresAt(Instant.now().plusSeconds(600));

        when(passwordResetOtpRepository.findTopByEmailAndUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("person@students.uz.ac.zw"), any()
        )).thenReturn(Optional.of(passwordResetOtp));
        when(passwordEncoder.matches("111111", "otp-hash")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> passwordResetService.confirmReset(
                new PasswordResetConfirmRequest("person@students.uz.ac.zw", "111111", "newSecret123")
        ));

        assertEquals("Invalid or expired reset code.", exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }
}