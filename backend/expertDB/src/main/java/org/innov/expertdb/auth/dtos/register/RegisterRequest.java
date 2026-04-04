package org.innov.expertdb.auth.dtos.register;

import org.innov.expertdb.user.Role;

public record RegisterRequest(
        String name,String surname, String email, String password, Role role) { }
