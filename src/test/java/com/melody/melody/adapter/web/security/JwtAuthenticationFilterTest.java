package com.melody.melody.adapter.web.security;

import com.melody.melody.adapter.web.user.CookieSupporter;
import com.melody.melody.domain.model.Identity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private TokenValidater tokenValidater;
    private TokenIssuanceService tokenIssuanceService;
    private UserDetailsServiceImpl userDetailsService;
    private CookieSupporter cookieSupporter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private String accessTokenName = "aaa";
    private String refreshTokenName = "ddd";

    @BeforeEach
    void setUp() {
        tokenValidater = Mockito.mock(TokenValidater.class);
        userDetailsService = Mockito.mock(UserDetailsServiceImpl.class);
        tokenIssuanceService = Mockito.mock(JwtTokenIssuanceService.class);
        cookieSupporter = Mockito.mock(CookieSupporter.class);

        filter = new JwtAuthenticationFilter(tokenValidater, tokenIssuanceService, userDetailsService, cookieSupporter);
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
    void doFilterInternal_ShouldSetAuthentication_WhenValidatedAccessToken() throws ServletException, IOException {
        String accessToken = "i_am_accesstoken";
        Identity userId = Identity.from(5L);

        when(request.getHeader(accessTokenName))
                .thenReturn(accessToken);

        when(tokenValidater.validateAccessToken(accessToken))
                .thenReturn(true);

        when(tokenValidater.getIdToAcessToken(accessToken))
                .thenReturn(userId.getValue());

        when(userDetailsService.loadUserById(userId))
                .thenReturn(Mockito.mock(UserDetails.class));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(request, times(1)).getHeader(accessTokenName);
        verify(tokenValidater, times(1)).validateAccessToken(accessToken);
        verify(tokenValidater, times(1)).getIdToAcessToken(accessToken);
        verify(userDetailsService, times(1)).loadUserById(userId);
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_AndCreateAccessToken_AndCreateRefreshToken_WhenNotValidatedAccessToken_AndValidatedRefreshToken() throws ServletException, IOException {
        String notValidatedAccessToken = "i_am_accesstoken";
        String validatedRefreshToken = "i_am_refreshtoken";

        String newAccessToken = "i_am_new_accesstoken";
        String newRefreshToken = "i_am_new_refreshtoken";
        String newRefreshTokenCookie = "i_am_new_refreshtoken_cookie";

        Identity userId = Identity.from(5L);

        when(request.getHeader(accessTokenName))
                .thenReturn(notValidatedAccessToken);

        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(refreshTokenName, validatedRefreshToken)});

        when(tokenValidater.validateAccessToken(notValidatedAccessToken))
                .thenReturn(false);

        when(tokenValidater.validateRefreshToken(validatedRefreshToken))
                .thenReturn(true);

        when(tokenValidater.getIdToRefreshToken(validatedRefreshToken))
                .thenReturn(userId.getValue());

        when(tokenIssuanceService.validateAndIssuance(userId, validatedRefreshToken))
                .thenReturn(new Token(userId, newRefreshToken, newAccessToken, LocalDateTime.now()));

        when(userDetailsService.loadUserById(userId))
                .thenReturn(Mockito.mock(UserDetails.class));


        when(cookieSupporter.getRefreshTokenCookie(newRefreshToken))
                .thenReturn(newRefreshTokenCookie);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(request, times(1)).getCookies();
        verify(request, times(1)).getHeader(accessTokenName);

        verify(response, times(1)).setHeader(accessTokenName, newAccessToken);
        verify(response, times(1)).setHeader(eq(HttpHeaders.SET_COOKIE), anyString());

        verify(cookieSupporter, times(1)).getRefreshTokenCookie(newRefreshToken);

        verify(tokenValidater, times(1)).validateAccessToken(notValidatedAccessToken);
        verify(tokenValidater, times(1)).validateRefreshToken(validatedRefreshToken);

        verify(tokenValidater, times(1)).getIdToRefreshToken(validatedRefreshToken);
        verify(tokenIssuanceService, times(1)).validateAndIssuance(userId, validatedRefreshToken);

        verify(userDetailsService, times(1)).loadUserById(userId);
    }

    @Test
    void doFilterInternal_ShouldDoNothing_WhenNotValidatedAccessToken_AndNotValidatedRefreshToken() throws ServletException, IOException {
        String notValidatedAccessToken = "i_am_not_validated_accesstoken";
        String notValidatedRefreshToken = "i_am_not_validated_refreshtoken";

        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(refreshTokenName, notValidatedRefreshToken)});

        when(request.getHeader(accessTokenName))
                .thenReturn(notValidatedAccessToken);

        when(tokenValidater.validateAccessToken(notValidatedAccessToken))
                .thenReturn(false);

        when(tokenValidater.validateRefreshToken(notValidatedRefreshToken))
                .thenReturn(false);


        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(request, times(1)).getCookies();
        verify(request, times(1)).getHeader(accessTokenName);

        verify(tokenValidater, times(1)).validateAccessToken(notValidatedAccessToken);
        verify(tokenValidater, times(1)).validateRefreshToken(notValidatedRefreshToken);
    }
}