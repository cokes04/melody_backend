package com.melody.melody.adapter.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class CustomMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return customMethodSecurityExpressionHandler;
    }
}