package com.melody.melody.adapter.web.security;

import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenManagerTest {
    private JwtTokenManager manager;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {

        jwtConfig = Mockito.mock(JwtConfig.class);
        manager = new JwtTokenManager(jwtConfig);
    }

    @Test
    void getIdToAcessToken_ShouldReturnId() {
        when(jwtConfig.getAccessToken())
                .thenReturn(getAccessTokenConfig());

        Identity userId = Identity.from(50L);
        String accessToken = manager.issuanceAccessToken(userId);

        long id = manager.getIdToAcessToken(accessToken);
        assertEquals(userId.getValue(), id);
    }

    @Test
    void getIdToRefreshTsoken_ShouldReturnId() {
        when(jwtConfig.getRefreshToken())
                .thenReturn(getRefreshTokenConfig());

        Identity userId = Identity.from(50L);
        String accessToken = manager.issuanceRefreshToken(userId);

        long id = manager.getIdToRefreshToken(accessToken);
        assertEquals(userId.getValue(), id);
    }

    @Test
    void validateAccessToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String refreshToken = manager.issuanceAccessToken(userId);

        boolean actual = manager.validateAccessToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateAccessToken_ShuoldReturnFalse_WhenCounterfeitAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();

        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String counterfeitAccessToken = manager.issuanceAccessToken(userId) + "위조";

        boolean actual = manager.validateAccessToken(counterfeitAccessToken);

        assertFalse(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnTrue() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String refreshToken = manager.issuanceRefreshToken(userId);

        boolean actual = manager.validateRefreshToken(refreshToken);

        assertTrue(actual);
    }

    @Test
    void validateRefreshToken_ShuoldReturnFalse_WhenCounterfeitRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String counterfeitRefreshToken = manager.issuanceRefreshToken(userId) + "위조";

        boolean actual = manager.validateRefreshToken(counterfeitRefreshToken);

        assertFalse(actual);
    }



    @Test
    void createAccessToken_ShuoldReturnAccessToken() {
        JwtConfig.Token token = getAccessTokenConfig();
        when(jwtConfig.getAccessToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String accessToken = manager.issuanceAccessToken(userId);

        assertNotNull(accessToken);

    }

    @Test
    void createRefreshToken_ShuoldReturnRefreshToken() {
        JwtConfig.Token token = getRefreshTokenConfig();

        when(jwtConfig.getRefreshToken())
                .thenReturn(token);

        Identity userId = TestUserDomainGenerator.randomUserId();
        String accessToken = manager.issuanceRefreshToken(userId);

        assertNotNull(accessToken);
    }


    private JwtConfig.Token getAccessTokenConfig(){
        return new JwtConfig.Token(10000, "accesssecretkey", "refreshtoken");
    }

    private JwtConfig.Token getRefreshTokenConfig(){
        return new JwtConfig.Token(33333, "refreshsecretkey", "refreshtoken");
    }
}