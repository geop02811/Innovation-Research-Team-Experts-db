package org.innov.expertdb.services;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.services.login.LoginService;
import org.innov.expertdb.services.signup.SignUpService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LoginService loginService;

    @Mock
    private SignUpService signUpService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest validLoginRequest;
    private LoginResponse mockLoginResponse;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest("john.doe@university.edu", "password123");
        mockLoginResponse = new LoginResponse("mock-jwt-token", "john.doe@university.edu", Role.USER);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        when(loginService.login(validLoginRequest)).thenReturn(mockLoginResponse);

        LoginResponse response = authService.login(validLoginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.token());
        assertEquals(validLoginRequest.email(), response.email());
        verify(loginService).login(validLoginRequest);
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowException() {
        when(loginService.login(validLoginRequest))
                .thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(validLoginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(loginService).login(validLoginRequest);
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest("john.doe@university.edu", "wrongpassword");

        when(loginService.login(invalidRequest))
                .thenThrow(new RuntimeException("Invalid email or password"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(invalidRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(loginService).login(invalidRequest);
    }

    @Test
    void login_WithInactiveAccount_ShouldThrowException() {
        when(loginService.login(validLoginRequest))
                .thenThrow(new RuntimeException("Account is not active. Please contact administrator."));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(validLoginRequest));

        assertEquals("Account is not active. Please contact administrator.", exception.getMessage());
        verify(loginService).login(validLoginRequest);
    }
}