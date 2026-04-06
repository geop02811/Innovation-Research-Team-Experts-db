package org.innov.expertdb.services;

import lombok.RequiredArgsConstructor;
import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.services.login.LoginService;
import org.innov.expertdb.services.signup.SignUpService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginService loginService;
    private final SignUpService signUpService;

    public RegisterResponse signUp(RegisterRequest request) {
        return signUpService.signUp(request);
    }

    public LoginResponse login(LoginRequest request) {
            return loginService.login(request);
    }

}
