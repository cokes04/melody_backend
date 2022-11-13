package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.User;

public interface TokenProvider {
    long getIdToAcessToken(String accessToken);
    long getIdToRefreshToken(String refreshToken);

    String createAccessToken(User.UserId id);
    String createRefreshToken(User.UserId id);

    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
}
