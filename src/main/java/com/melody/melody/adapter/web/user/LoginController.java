package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.security.TokenProvider;
import com.melody.melody.adapter.web.user.request.LoginRequest;
import com.melody.melody.adapter.web.user.response.AuthenticationResponse;
import com.melody.melody.application.service.authentication.AuthenticationService;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class LoginController {
    private final AuthenticationService authenticationService;
    private final TokenProvider tokenProvider;
    private final CookieSupporter cookieSupporter;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        AuthenticationService.Command command = request.toCommand();
        AuthenticationService.Result result = authenticationService.execute(command);

        Identity userId = result.getUser().getId();
        String accessToken = tokenProvider.createAccessToken(userId);
        String refreshToken = tokenProvider.createRefreshToken(userId);
        String refreshTokenCookie = cookieSupporter.getRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new AuthenticationResponse(accessToken));
    }
}
