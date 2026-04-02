t stapackage org.innov.expertdb.services;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse createUser(RegisterRequest request) {
        // 1. Map DTO to Entity
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        // Hash The Password
        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPasswordHash(hashedPassword);

        // 2. Save to database
        User savedUser = userRepository.save(user);

        // 3. Map Entity back to DTO
        return mapToResponse(savedUser);
    }

    public List<RegisterResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse) // Convert every entity to a DTO
                .collect(Collectors.toList());
    }

    public RegisterResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public RegisterResponse userLogIn(LoginRequest request) {
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
    private RegisterResponse mapToResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}