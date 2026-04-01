package org.innov.expertdb.auth.dtos.login;

import org.innov.expertdb.user.Role;

public record LoginResponse(
        String token,
        String email,
        Role role
) {
}
