package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;

public interface TokenProvider {
    long getIdToAcessToken(String accessToken);
    long getIdToRefreshToken(String refreshToken);

    String createAccessToken(Identity userId);
    String createRefreshToken(Identity userId);

    boolean validateAccessToken(String accessToken);
    boolean validateRefreshToken(String refreshToken);
}
