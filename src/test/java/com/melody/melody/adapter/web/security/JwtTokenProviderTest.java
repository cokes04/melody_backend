package com.melody.melody.adapter.web.security;

import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
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

        User.UserId userId = new User.UserId(50L);
        String accessToken = provider.createAccessToken(userId);

        long id = provider.getIdToAcessToken(accessToken);
        assertEquals(userId.getValue().longValue(), id);
    }


    @Test
    void getIdToRefreshTsoken_ShouldReturnId() {
        when(jwtConfig.getRefreshToken())
                .thenReturn(getRefreshTokenConfig());

        User.UserId userId = new User.UserId(50L);
        String accessToken = provider.createRefreshToken(userId);

        long id = provider.getIdToRefreshToken(accessToken);
        assertEquals(userId.getValue().longValue(), id);
    }

    @Test
    void createAccessToken_ShuoldReturnAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();
        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String accessToken = provider.createAccessToken(id);

        assertNotNull(accessToken);

    }

    @Test
    void createRefreshToken_ShuoldReturnRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String accessToken = provider.createRefreshToken(id);

        assertNotNull(accessToken);
    }

    @Test
    void validateAccessToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String refreshToken = provider.createAccessToken(id);

        boolean actual = provider.validateAccessToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateAccessToken_ShuoldReturnFalse_WhenCounterfeitAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String counterfeitAccessToken = provider.createAccessToken(id) + "위조";

        boolean actual = provider.validateAccessToken(counterfeitAccessToken);

        assertFalse(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String refreshToken = provider.createRefreshToken(id);

        boolean actual = provider.validateRefreshToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnFalse_WhenCounterfeitRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        User.UserId id = TestUserDomainGenerator.randomUserId();
        String counterfeitRefreshToken = provider.createRefreshToken(id) + "위조";

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