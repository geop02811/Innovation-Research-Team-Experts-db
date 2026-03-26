package org.innov.expertdb.dtos;

public record UserLoginRequest(
    String email,
    String password
) {}
