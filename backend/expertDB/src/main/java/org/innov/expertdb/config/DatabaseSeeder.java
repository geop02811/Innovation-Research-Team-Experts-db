package org.innov.expertdb.config;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@expertdb.com";

        // 1. Check if the admin already exists (so we don't crash trying to create them twice)
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            
            log.info("Admin user not found. Seeding default Admin...");

            // 2. We use our DTO and Service so the password gets hashed automatically!
            RegisterRequest adminRequest = new RegisterRequest(
                    "Super Admin",
                    "Smith",
                    adminEmail,
                    "AdminPassword123!",
                    null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null
            );

            userService.createUser(adminRequest, AccountStatus.ACTIVE, Role.ADMIN);
            
            log.info("Default Admin seeded successfully!");
        } else {
            log.info("Admin user already exists. Skipping database seeding.");
        }
    }
}