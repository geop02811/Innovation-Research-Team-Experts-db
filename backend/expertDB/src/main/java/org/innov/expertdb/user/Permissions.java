package org.innov.expertdb.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permissions {

    VIEWER_READ("viewer:read"),
    VIEWER_UPDATE("viewer:update"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_APPROVE("admin:approve"),
    ADMIN_DELETE("admin:delete");

    @Getter
    private final String permission;

}
