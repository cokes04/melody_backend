package com.melody.melody.adapter.web.security;

import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {
    private JwtTokenProvider provider;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = Mockito.mock(JwtConfig.class);
        provider = new JwtTokenProvider(jwtConfig);
    }

    @Test
    void getIdToAcessToken_ShouldReturnId() {
        when(jwtConfig.getAccessToken())
                .thenReturn(getAccessTokenConfig());

        Identity userId = Identity.from(50L);
        String accessToken = provider.createAccessToken(userId);

        long id = provider.getIdToAcessToken(accessToken);
        assertEquals(userId.getValue(), id);
    }


    @Test
    void getIdToRefreshTsoken_ShouldReturnId() {
        when(jwtConfig.getRefreshToken())
                .thenReturn(getRefreshTokenConfig());

        Identity userId = Identity.from(50L);
        String accessToken = provider.createRefreshToken(userId);

        long id = provider.getIdToRefreshToken(accessToken);
        assertEquals(userId.getValue(), id);
    }

    @Test
    void createAccessToken_ShuoldReturnAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();
        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String accessToken = provider.createAccessToken(userId);

        assertNotNull(accessToken);

    }

    @Test
    void createRefreshToken_ShuoldReturnRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String accessToken = provider.createRefreshToken(userId);

        assertNotNull(accessToken);
    }

    @Test
    void validateAccessToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String refreshToken = provider.createAccessToken(userId);

        boolean actual = provider.validateAccessToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateAccessToken_ShuoldReturnFalse_WhenCounterfeitAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String counterfeitAccessToken = provider.createAccessToken(userId) + "위조";

        boolean actual = provider.validateAccessToken(counterfeitAccessToken);

        assertFalse(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String refreshToken = provider.createRefreshToken(userId);

        boolean actual = provider.validateRefreshToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnFalse_WhenCounterfeitRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String counterfeitRefreshToken = provider.createRefreshToken(userId) + "위조";

        boolean actual = provider.validateRefreshToken(counterfeitRefreshToken);

        assertFalse(actual);
    }


    private JwtConfig.Token getAccessTokenConfig(){
        return new JwtConfig.Token(10000, "accesssecretkey", "refreshtoken");
    }

    private JwtConfig.Token getRefreshTokenConfig(){
        return new JwtConfig.Token(33333, "refreshsecretkey", "refreshtoken");
    }
}