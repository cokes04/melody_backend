package com.melody.melody.adapter.web.security;

public interface TokenValidater {
    long getIdToAcessToken(String accessToken);
    long getIdToRefreshToken(String refreshToken);

    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
}
