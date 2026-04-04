package org.innov.expertdb.services.signup;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SignUpService signUpService;

    private RegisterRequest validRequest;
    private RegisterResponse expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@university.edu",
                "password123",
                Role.USER
        );

        expectedResponse = new RegisterResponse(
                UUID.randomUUID(),
                "John",
                "john.doe@university.edu",
                Role.USER,
                AccountStatus.PENDING
        );
    }

    @Test
    void signUp_WithNewUser_ShouldCreateUserWithPendingStatus() {
        // Arrange
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.empty());
        when(userService.createUser(validRequest, AccountStatus.PENDING)).thenReturn(expectedResponse);

        // Act
        RegisterResponse response = signUpService.signUp(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AccountStatus.PENDING, response.status());
        assertEquals(validRequest.email(), response.email());
        
        verify(userRepository).findByEmail(validRequest.email());
        verify(userService).createUser(validRequest, AccountStatus.PENDING);
    }

    @Test
    void signUp_WithExistingActiveUser_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail(validRequest.email());
        existingUser.setStatus(AccountStatus.ACTIVE);
        
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            signUpService.signUp(validRequest);
        });
        
        assertEquals("User with this email already exists and is active", exception.getMessage());
        
        verify(userRepository).findByEmail(validRequest.email());
        verify(userService, never()).createUser(any(), any());
    }

    @Test
    void signUp_WithExistingPendingUser_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail(validRequest.email());
        existingUser.setStatus(AccountStatus.PENDING);
        
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            signUpService.signUp(validRequest);
        });
        
        assertEquals("Registration with this email is already pending approval", exception.getMessage());
        
        verify(userRepository).findByEmail(validRequest.email());
        verify(userService, never()).createUser(any(), any());
    }

    @Test
    void signUp_WithExistingApprovedUser_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail(validRequest.email());
        existingUser.setStatus(AccountStatus.APPROVED);
        
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            signUpService.signUp(validRequest);
        });
        
        assertEquals("User with this email already exists and is approved", exception.getMessage());
        
        verify(userRepository).findByEmail(validRequest.email());
        verify(userService, never()).createUser(any(), any());
    }

    @Test
    void signUp_WithExistingDisabledUser_ShouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail(validRequest.email());
        existingUser.setStatus(AccountStatus.DISABLED);
        
        when(userRepository.findByEmail(validRequest.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            signUpService.signUp(validRequest);
        });
        
        assertEquals("This account is disabled. Contact administrator for reactivation.", exception.getMessage());
        
        verify(userRepository).findByEmail(validRequest.email());
        verify(userService, never()).createUser(any(), any());
    }
}