package org.innov.expertdb.services;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequest validLoginRequest;
    private UserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest("john.doe@university.edu", "password123");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@university.edu");
        user.setRole(Role.USER);
        user.setStatus(AccountStatus.ACTIVE);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("hashedPassword")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(customUserDetailsService.loadUserByUsername(validLoginRequest.email()))
                .thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mock-jwt-token");

        // Act
        LoginResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.token());
        assertEquals(validLoginRequest.email(), response.email());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService).loadUserByUsername(validLoginRequest.email());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(validLoginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("john.doe@university.edu", "wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(invalidRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(validLoginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any());
    }
}