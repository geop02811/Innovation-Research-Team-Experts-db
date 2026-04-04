package org.innov.expertdb.auth.dtos.register;

import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;

import java.util.UUID;


public record RegisterResponse(
        UUID id,
        String name,
        // String surname,
        String email,
        Role role,
        AccountStatus status

) {
}
