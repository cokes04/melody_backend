package com.melody.melody.adapter.web.security;

import com.melody.melody.adapter.web.user.CookieSupporter;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidater tokenValidater;
    private final TokenIssuanceService issuanceService;
    private final UserDetailsServiceImpl userDetailsService;
    private final CookieSupporter cookieSupporter;

    @Value(value = "${app.jwt.accessToken.name}")
    private String accessTokenName;

    @Value(value = "${app.jwt.refreshToken.name}")
    private String refreshTokenName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = getAccessTokenToRequest(request);

        // 유효한 accessToken O
        if ( StringUtils.hasText(accessToken) && tokenValidater.validateAccessToken(accessToken) ){
            Identity userId = Identity.from(tokenValidater.getIdToAcessToken(accessToken));

            UserDetails userDetails = userDetailsService.loadUserById(userId);
            setAuthentication(request, userDetails);

        }else {
            String refreshToken = getRefreshTokenToRequest(request);

            // 유효한 refreshToken O
            if ( StringUtils.hasText(refreshToken) && tokenValidater.validateRefreshToken(refreshToken)) {
                Identity userId = Identity.from(tokenValidater.getIdToRefreshToken(refreshToken));

                String idAddress = request.getRemoteAddr();
                Token token = issuanceService.validateAndIssuance(userId, refreshToken);

                response.setHeader(accessTokenName, token.getAccessToken());
                response.setHeader(HttpHeaders.SET_COOKIE, cookieSupporter.getRefreshTokenCookie(token.getRefreshToken()));

                UserDetails userDetails = userDetailsService.loadUserById(userId);
                setAuthentication(request, userDetails);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getAccessTokenToRequest(HttpServletRequest request){
        return request.getHeader(accessTokenName);
    }

    private String getRefreshTokenToRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter( c -> c.getName().equals(refreshTokenName) )
                .findFirst()
                .map(Cookie::getValue)
                .orElseGet(() -> null);
    }
}
