package com.melody.melody.adapter.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot{

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

}