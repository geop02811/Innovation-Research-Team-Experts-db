package org.innov.expertdb.services.signup;

import java.util.Optional;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public RegisterResponse signUp(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        // Step 1: Check if user exists by email
        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            AccountStatus currentStatus = user.getStatus();
            
            // Step 2: If user exists with ACTIVE or PENDING status, reject signup
            if (currentStatus == AccountStatus.ACTIVE) {
                throw new RuntimeException("An account with this email already exists.");
            }
            
            if (currentStatus == AccountStatus.PENDING) {
                throw new RuntimeException("Registration with this email is already pending approval");
            }
            
            if (currentStatus == AccountStatus.APPROVED) {
                throw new RuntimeException("An account with this email already exists.");
            }
            
            // For DISABLED status, you could implement reactivation here later
            if (currentStatus == AccountStatus.DISABLED) {
                throw new RuntimeException("This account is disabled. Contact administrator for reactivation.");
            }
        }
        
        // Step 3: Create new user with PENDING status
        // The userService.createUser method handles password hashing and saving
        RegisterResponse response = userService.createUser(request, AccountStatus.PENDING, Role.USER);
        
        // Step 4: Return response with PENDING status
        return response;
    }
}