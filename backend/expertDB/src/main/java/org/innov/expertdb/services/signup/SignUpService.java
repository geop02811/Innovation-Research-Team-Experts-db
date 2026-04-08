package org.innov.expertdb.services.signup;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.NotificationService;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.Optional;



@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;


    @Transactional
    public RegisterResponse signUp(RegisterRequest request) {
        // Step 1: Check if user exists by email
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            AccountStatus currentStatus = user.getStatus();
            
            // Step 2: If user exists with ACTIVE or PENDING status, reject signup
            if (currentStatus == AccountStatus.ACTIVE) {
                throw new RuntimeException("User with this email already exists and is active");
            }
            
            if (currentStatus == AccountStatus.PENDING) {
                throw new RuntimeException("Registration with this email is already pending approval");
            }
            
            if (currentStatus == AccountStatus.APPROVED) {
                throw new RuntimeException("User with this email already exists and is approved");
            }
            
            // For DISABLED status, you could implement reactivation here later
            if (currentStatus == AccountStatus.DISABLED) {
                throw new RuntimeException("This account is disabled. Contact administrator for reactivation.");
            }
        }
        
        // Step 3: Create new user with PENDING status
        // The userService.createUser method handles password hashing and saving
        RegisterResponse response = userService.createUser(request, AccountStatus.PENDING);

           // Fetch the saved user to get the User entity
        User savedUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User saved but not found"));
        
        // Notify all admins about the pending approval
        notificationService.notifyAdminsOfPendingUser(savedUser);


        // Step 4: Return response with PENDING status
        return response;
    }
}