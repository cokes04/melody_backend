package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final String email;
    private final String password;
    private final long userId;

    private Collection<GrantedAuthority> authorities;

    public UserDetailsImpl(User user){
        this.email = user.getEmail();
        this.password = user.getPassword().getEncryptedString();

        this.userId = user.getId().map(User.UserId::getValue)
                .orElseThrow(() -> new FailedAuthenticationException(DomainError.of(UserErrorType.Authentication_Failed)));

        this.authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
    }

    @Builder
    public UserDetailsImpl(String email, String password, long userId, Collection<GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public long getUserId(){
        return this.userId;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
