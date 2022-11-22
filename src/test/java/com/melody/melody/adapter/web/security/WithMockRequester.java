package com.melody.melody.adapter.web.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockRequesterSecurityContextFactory.class)
public @interface WithMockRequester {

    String email() default "";

    long userId() default 0;

    String[] role() default "ROLE_USER";

}