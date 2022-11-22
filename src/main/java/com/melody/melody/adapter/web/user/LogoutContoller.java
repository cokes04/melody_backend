package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class LogoutContoller {
    private final JwtConfig jwtConfig;

    @Value("${app.rest.logout.redirectUri}")
    private String logoutRedirectUri;

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String removedRefreshTokenCookie = removeRefreshTokenCookie();

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.SET_COOKIE, removedRefreshTokenCookie)
                .location(URI.create(logoutRedirectUri))
                .build();
    }

    private String removeRefreshTokenCookie(){
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
