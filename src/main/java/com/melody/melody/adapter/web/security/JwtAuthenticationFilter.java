package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Value(value = "${app.jwt.accessToken.name}")
    private String accessTokenName;

    @Value(value = "${app.jwt.refreshToken.name}")
    private String refreshTokenName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = getAccessTokenToRequest(request);

        if ( StringUtils.hasText(accessToken) && jwtTokenProvider.validateAccessToken(accessToken) ){
            User.UserId userId = new User.UserId(
                    jwtTokenProvider.getIdToAcessToken(accessToken)
            );
            UserDetails userDetails = userDetailsService.loadUserById(userId);
            setAuthentication(request, userDetails);

        }else {
            String refreshToken = getRefreshTokenToRequest(request);

            if ( StringUtils.hasText(refreshToken) && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                User.UserId userId = new User.UserId(
                        jwtTokenProvider.getIdToRefreshToken(refreshToken)
                );

                String newAccessToken = jwtTokenProvider.createAccessToken(userId);
                response.setHeader(accessTokenName, newAccessToken);

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
        return Arrays.stream(request.getCookies())
                .filter( c -> c.getName().equals(refreshTokenName) )
                .findFirst()
                .map(Cookie::getValue)
                .orElseGet(() -> null);
    }
}
