package com.melody.melody.adapter.web.user;

import com.melody.melody.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieSupporter {
    private final JwtConfig jwtConfig;

    public String getRefreshTokenCookie(String refreshToken){
        return ResponseCookie
                .from(jwtConfig.getRefreshToken().getName(), refreshToken)
                .httpOnly(true)
                .secure(false)
                .path(null)
                .domain(null)
                .maxAge(jwtConfig.getRefreshToken().getValidMilliSecond() / 1000)
                .build()
                .toString();
    }

    public String removeRefreshTokenCookie(){
        return ResponseCookie
                .from(jwtConfig.getRefreshToken().getName(), null)
                .httpOnly(true)
                .secure(false)
                .path(null)
                .domain(null)
                .maxAge(0)
                .build()
                .toString();
    }
}
