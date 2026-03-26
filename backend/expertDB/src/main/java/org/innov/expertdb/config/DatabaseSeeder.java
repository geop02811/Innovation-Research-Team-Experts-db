package org.innov.expertdb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innov.expertdb.dtos.UserCreateRequest;
import org.innov.expertdb.models.Role;
import org.innov.expertdb.repositories.UserRepository;
import org.innov.expertdb.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j // Lombok annotation for logging to the console
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
            UserCreateRequest adminRequest = new UserCreateRequest(
                    "Super Admin",
                    adminEmail,
                    "AdminPassword123!", // Raw password (will be hashed by UserService)
                    Set.of(Role.ADMIN, Role.EXPERT, Role.VIEWER) // Give them multiple roles!
            );

            userService.createUser(adminRequest);
            
            log.info("Default Admin seeded successfully!");
        } else {
            log.info("Admin user already exists. Skipping database seeding.");
        }
    }
}