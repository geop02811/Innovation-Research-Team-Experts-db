package org.innov.expertdb.auth.dtos.login;

import java.util.UUID;

import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;

public record LoginResponse(
        String token,
        UUID id,
        String email,
        String fullName,
        Role role,
        AccountStatus status
) {
}
