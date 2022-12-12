package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import lombok.Setter;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public abstract class AbstractSecurityExpression {
    @Setter
    private Authentication authentication;

    protected Optional<UserDetailsImpl> getUserPrincipal(){
        if (authentication == null) return Optional.empty();

        Object userPrincipal = authentication.getPrincipal();

        if (userPrincipal.equals("anonymousUser")) return Optional.empty();
        else return Optional.of((UserDetailsImpl) userPrincipal);
    }
}