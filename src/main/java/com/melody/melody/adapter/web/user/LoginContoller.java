package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.security.TokenProvider;
import com.melody.melody.adapter.web.user.request.LoginRequest;
import com.melody.melody.adapter.web.user.response.AuthenticationResponse;
import com.melody.melody.application.service.authentication.AuthenticationService;
import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class LoginContoller {
    private final AuthenticationService authenticationService;
    private final TokenProvider tokenProvider;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        AuthenticationService.Command command = request.toCommand();
        AuthenticationService.Result result = authenticationService.execute(command);

        User.UserId id = result.getUser().getId().get();
        String accessToken = tokenProvider.createAccessToken(id);
        String refreshToken = tokenProvider.createRefreshToken(id);
        String refreshTokenCookie = getRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new AuthenticationResponse(accessToken));
    }

    private String getRefreshTokenCookie(String refreshToken){
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
}
