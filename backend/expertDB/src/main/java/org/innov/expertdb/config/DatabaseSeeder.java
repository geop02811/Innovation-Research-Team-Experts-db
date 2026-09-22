package org.innov.expertdb.config;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private static final String TEST_PASSWORD = "TestPassword123!";

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        seed(new RegisterRequest(
                "Super Admin",
                "Smith",
                "admin@expertdb.com",
                "AdminPassword123!",
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        ), Role.ADMIN);

        String gushaResearch = "Appropriate Animal Husbandry Technology, Animal Improvement and Genetics, "
                + "Rangeland and Pasture Nutrition, Forage and Fodder Production and Conservation";
        seed(new RegisterRequest(
                "Jacob",
                "Gusha",
                "jgusha.test@vet.uz.ac.zw",
                TEST_PASSWORD,
                "Dr",
                "Dr Jacob Gusha",
                "+263772252514",
                "PhD Agric, MSc Animal Science, BSc Agric Honours in Animal Science",
                gushaResearch,
                null,
                "Director",
                "jgusha.test@vet.uz.ac.zw",
                "+263772252514",
                "PhD Agric",
                null,
                null,
                null,
                "Agribusiness and Continuing Education",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                gushaResearch,
                null,
                "Publications: 28 journal articles, 10 book manuals",
                null,
                null,
                null,
                null,
                null
        ), Role.USER);
    }

    // Seeded accounts skip signup OTP, so they're created already verified.
    private void seed(RegisterRequest request, Role role) {
        User user = userRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    log.info("Seeding {}", request.email());
                    var created = userService.createUser(request, AccountStatus.ACTIVE, role);
                    return userRepository.findById(created.id()).orElseThrow();
                });

        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
