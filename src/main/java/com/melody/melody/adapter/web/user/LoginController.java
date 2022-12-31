package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.security.Token;
import com.melody.melody.adapter.web.security.TokenIssuanceService;
import com.melody.melody.adapter.web.user.request.LoginRequest;
import com.melody.melody.adapter.web.user.response.AuthenticationResponse;
import com.melody.melody.application.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class LoginController {
    private final TokenIssuanceService tokenIssuanceService;
    private final AuthenticationService authenticationService;
    private final CookieSupporter cookieSupporter;

    @ModelAttribute("ipAddress")
    public String getIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@ModelAttribute("ipAddress") String ipAddress, @RequestBody LoginRequest request){
        AuthenticationService.Command command = request.toCommand();
        AuthenticationService.Result result = authenticationService.execute(command);

        Token token = tokenIssuanceService.issuance(result.getUser().getId());
        String refreshTokenCookie = cookieSupporter.getRefreshTokenCookie(token.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new AuthenticationResponse(token.getAccessToken()));
    }
}
