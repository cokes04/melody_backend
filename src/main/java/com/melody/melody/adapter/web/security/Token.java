package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Token {
    private final Identity userId;

    private final String refreshToken;

    private final String accessToken;

    private final LocalDateTime lastUpdatedDate;

    public static Token create(Identity userId, String refreshToken, String accessToken){
        return new Token(userId, refreshToken, accessToken, LocalDateTime.now());
    }

    public boolean validate(String refreshToken){
        return this.refreshToken.equals(refreshToken);
    }

    /*
    public boolean validate(Device device, String ipAddress){
        return this.device.equals(device) && this.ipAddress.equals(ipAddress);
    }*/
}
