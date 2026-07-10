package org.innov.expertdb.services.login;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.JwtService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.password(),user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");
        }

        if(user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active. Please contact administrator.");
        }

        String token = jwtService.generateToken(user);
        String fullName = (user.getName() + " " + user.getSurname()).trim();
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                fullName,
                user.getRole(),
                user.getStatus()
        );
    }
}
