package org.innov.expertdb.classes;

import lombok.RequiredArgsConstructor;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //later to add AccountSatus logic
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == AccountStatus.ACTIVE;
    }

}
