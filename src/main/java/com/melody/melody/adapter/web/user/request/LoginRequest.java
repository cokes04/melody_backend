package com.melody.melody.adapter.web.user.request;

import com.melody.melody.application.service.authentication.AuthenticationService;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRequest {
    private final String email;
    private final String password;

    public AuthenticationService.Command toCommand(){
        return new AuthenticationService.Command(email, password);
    }
}
