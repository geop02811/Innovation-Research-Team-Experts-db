package org.innov.expertdb.dtos;

import org.innov.expertdb.models.Role;
import java.util.Set;

// Notice: NO 'id' here! Swagger will not ask for it.
// We DO ask for a raw password here, which we will hash later.
public record UserCreateRequest(
    String name,
    String email,
    String password,
    Set<Role> roles
) {}