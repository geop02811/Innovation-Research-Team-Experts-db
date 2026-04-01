package org.innov.expertdb.auth.dtos.login;

public record LoginRequest(
        String email,
        String password
) {
}
