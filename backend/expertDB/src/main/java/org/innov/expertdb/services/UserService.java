package org.innov.expertdb.services;

import lombok.RequiredArgsConstructor;
import org.innov.expertdb.dtos.UserCreateRequest;
import org.innov.expertdb.dtos.UserLoginRequest;
import org.innov.expertdb.dtos.UserResponse;
import org.innov.expertdb.models.User;
import org.innov.expertdb.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 1. Map DTO to Entity
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRoles(request.roles());
        
        // Hash The Password
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPasswordHash(hashedPassword);

        // 2. Save to database
        User savedUser = userRepository.save(user);

        // 3. Map Entity back to DTO
        return mapToResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse) // Convert every entity to a DTO
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse userLogIn(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if the password matches
        if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return mapToResponse(user);
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    // Helper method to keep code clean
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles()
        );
    }
}