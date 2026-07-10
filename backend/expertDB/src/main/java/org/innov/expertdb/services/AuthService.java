package org.innov.expertdb.services;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.login.LoginResponse;
import org.innov.expertdb.auth.dtos.password.PasswordResetConfirmRequest;
import org.innov.expertdb.auth.dtos.password.PasswordResetRequest;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
import org.innov.expertdb.services.login.LoginService;
import org.innov.expertdb.services.password.PasswordResetService;
import org.innov.expertdb.services.signup.SignUpService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginService loginService;
    private final SignUpService signUpService;
    private final PasswordResetService passwordResetService;

    public RegisterResponse signUp(RegisterRequest request) {
        return signUpService.signUp(request);
    }

    public LoginResponse login(LoginRequest request) {
            return loginService.login(request);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        passwordResetService.requestReset(request);
    }

    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        passwordResetService.confirmReset(request);
    }

}
