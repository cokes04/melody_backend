package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WithMockRequesterSecurityContextFactory implements WithSecurityContextFactory<WithMockRequester> {
	@Override
	public SecurityContext createSecurityContext(WithMockRequester requester) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		UserDetailsImpl principal = UserDetailsImpl.builder()
				.userId(requester.userId())
				.email(requester.email())
				.password(null)
				.authorities(Arrays.stream(requester.role()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
				.build();

		Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}