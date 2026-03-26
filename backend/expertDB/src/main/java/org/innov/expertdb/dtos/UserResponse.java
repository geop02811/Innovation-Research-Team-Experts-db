package org.innov.expertdb.dtos;

import org.innov.expertdb.models.Role;
import java.util.Set;
import java.util.UUID;

// We return the ID so the frontend has it, but NEVER the password.
public record UserResponse(
    UUID id,
    String name,
    String email,
    Set<Role> roles
) {}