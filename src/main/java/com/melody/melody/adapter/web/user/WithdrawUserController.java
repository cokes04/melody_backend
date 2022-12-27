package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.application.service.user.WithdrawUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class WithdrawUserController {
    private final WithdrawUserService service;
    private final CookieSupporter cookieSupporter;

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> withdraw(@NotNull @Positive @PathVariable("userId") long userId){
        service.execute(new WithdrawUserService.Command(userId));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieSupporter.removeRefreshTokenCookie())
                .build();
    }

}

