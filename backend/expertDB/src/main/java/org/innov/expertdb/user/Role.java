package org.innov.expertdb.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                  Permissions.ADMIN_READ,
                  Permissions.ADMIN_WRITE,
                  Permissions.ADMIN_DELETE,
                  Permissions.ADMIN_APPROVE,
                  Permissions.VIEWER_UPDATE,
                  Permissions.VIEWER_READ
            )
    ),
    VIEWER(
            Set.of(
                    Permissions.VIEWER_UPDATE,
                    Permissions.VIEWER_READ
            )
    );

    @Getter
    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }
}
