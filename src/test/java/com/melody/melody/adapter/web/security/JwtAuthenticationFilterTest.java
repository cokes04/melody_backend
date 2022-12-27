package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsServiceImpl userDetailsService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private String accessTokenName = "aaa";
    private String refreshTokenName = "ddd";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        userDetailsService = Mockito.mock(UserDetailsServiceImpl.class);

        filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        ReflectionTestUtils.setField(filter, "accessTokenName", accessTokenName);
        ReflectionTestUtils.setField(filter, "refreshTokenName", refreshTokenName);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenValidateDAccessToken() throws ServletException, IOException {
        String accessToken = "i_am_accesstoken";
        Identity userId = Identity.from(5L);

        when(request.getHeader(eq(accessTokenName)))
                .thenReturn(accessToken);

        when(jwtTokenProvider.validateAccessToken(eq(accessToken)))
                .thenReturn(true);

        when(jwtTokenProvider.getIdToAcessToken(accessToken))
                .thenReturn(userId.getValue());

        when(userDetailsService.loadUserById(eq(userId)))
                .thenReturn(Mockito.mock(UserDetails.class));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(request, times(1)).getHeader(eq(accessTokenName));
        verify(jwtTokenProvider, times(1)).validateAccessToken(eq(accessToken));
        verify(jwtTokenProvider, times(1)).getIdToAcessToken(eq(accessToken));
        verify(userDetailsService, times(1)).loadUserById(eq(userId));
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_And_CreateAccessToken_WhenNotValidateAccessToken_And_ValidateRefreshToken() throws ServletException, IOException {
        String notValidatedAccessToken = "i_am_accesstoken";
        String refreshToken = "i_am_refreshtoken";
        String newAccessToken = "i_am_new_accesstoken";

        Identity userId = Identity.from(5L);

        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(refreshTokenName, refreshToken)});

        when(request.getHeader(eq(accessTokenName)))
                .thenReturn(notValidatedAccessToken);

        when(jwtTokenProvider.validateAccessToken(eq(notValidatedAccessToken)))
                .thenReturn(false);

        when(jwtTokenProvider.validateRefreshToken(eq(refreshToken)))
                .thenReturn(true);

        when(jwtTokenProvider.getIdToRefreshToken(refreshToken))
                .thenReturn(userId.getValue());

        when(jwtTokenProvider.createAccessToken(eq(userId)))
                .thenReturn(newAccessToken);

        when(userDetailsService.loadUserById(eq(userId)))
                .thenReturn(Mockito.mock(UserDetails.class));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(request, times(1)).getCookies();
        verify(request, times(1)).getHeader(eq(accessTokenName));

        verify(response, times(1)).setHeader(eq(accessTokenName), eq(newAccessToken));

        verify(jwtTokenProvider, times(1)).validateAccessToken(eq(notValidatedAccessToken));
        verify(jwtTokenProvider, times(1)).validateRefreshToken(eq(refreshToken));

        verify(jwtTokenProvider, times(1)).getIdToRefreshToken(eq(refreshToken));
        verify(jwtTokenProvider, times(1)).createAccessToken(eq(userId));

        verify(userDetailsService, times(1)).loadUserById(eq(userId));
    }

    @Test
    void doFilterInternal_ShouldDoNothing_WhenNoValidateAccessToken_And_NotValidateRefreshToken() throws ServletException, IOException {
        String notValidatedAccessToken = "i_am_not_validated_accesstoken";
        String notValidatedRefreshToken = "i_am_not_validated_refreshtoken";

        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(refreshTokenName, notValidatedRefreshToken)});

        when(request.getHeader(eq(accessTokenName)))
                .thenReturn(notValidatedAccessToken);

        when(jwtTokenProvider.validateAccessToken(eq(notValidatedAccessToken)))
                .thenReturn(false);

        when(jwtTokenProvider.validateRefreshToken(eq(notValidatedRefreshToken)))
                .thenReturn(false);


        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request, times(1)).getCookies();
        verify(request, times(1)).getHeader(eq(accessTokenName));

        verify(jwtTokenProvider, times(1)).validateAccessToken(eq(notValidatedAccessToken));
        verify(jwtTokenProvider, times(1)).validateRefreshToken(eq(notValidatedRefreshToken));
    }
}