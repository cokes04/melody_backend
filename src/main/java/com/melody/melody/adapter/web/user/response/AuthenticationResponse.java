package com.melody.melody.adapter.web.user.response;

import lombok.Value;

@Value
public class AuthenticationResponse {
    private final String token;
}