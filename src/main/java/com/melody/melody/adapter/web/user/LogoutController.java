package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class LogoutController {
    private final CookieSupporter cookieSupporter;

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieSupporter.removeRefreshTokenCookie())
                .build();
    }
}
