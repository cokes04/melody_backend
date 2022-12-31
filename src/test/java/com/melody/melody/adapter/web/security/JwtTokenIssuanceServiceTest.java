package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JwtTokenIssuanceServiceTest {
    private JwtTokenIssuanceService service;
    private TokenIssuancer issuancer;
    private TokenRepository repository;

    @BeforeEach
    void setUp() {
        issuancer = Mockito.mock(TokenIssuancer.class);
        repository = Mockito.mock(TokenRepository.class);

        service = new JwtTokenIssuanceService(issuancer, repository);
    }

    @Test
    void issuance_ShouldReturnToken() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        String accessToken = "acc";
        String resfreshToken = "ref";

        when(issuancer.issuanceAccessToken(userId))
                .thenReturn(accessToken);

        when(issuancer.issuanceRefreshToken(userId))
                .thenReturn(resfreshToken);

        when(repository.save(any(Token.class)))
                .thenAnswer( a -> a.getArgument(0, Token.class));

        Token actual = service.issuance(userId);

        assertEquals(userId, actual.getUserId());
        assertEquals(accessToken, actual.getAccessToken());
        assertEquals(resfreshToken, actual.getRefreshToken());
        assertNotNull(actual.getLastUpdatedDate());
    }

    @Test
    void validateAndIssuance_ShouldReturnToken_WhenValidatedRefreshToken() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        String savedResfreshToken = "savedref";
        String resfreshToken = "savedref";

        String newAccessToken = "newacc";
        String newResfreshToken = "newref";

        when(issuancer.issuanceAccessToken(userId))
                .thenReturn(newAccessToken);

        when(issuancer.issuanceRefreshToken(userId))
                .thenReturn(newResfreshToken);

        when(repository.findBy(userId))
                .thenReturn(Optional.of(new Token(userId, savedResfreshToken, "None", LocalDateTime.now())));

        when(repository.save(any(Token.class)))
                .thenAnswer( a -> a.getArgument(0, Token.class));

        Token actual = service.validateAndIssuance(userId, resfreshToken);

        assertEquals(userId, actual.getUserId());
        assertEquals(newAccessToken, actual.getAccessToken());
        assertEquals(newResfreshToken, actual.getRefreshToken());
        assertNotNull(actual.getLastUpdatedDate());
    }

    @Test
    void validateAndIssuance_ShouldReturnToken_WhenNotValidatedRefreshToken() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        String savedResfreshToken = "savedref";
        String resfreshToken = "ref";

        when(repository.findBy(userId))
                .thenReturn(Optional.of(new Token(userId, savedResfreshToken, "None", LocalDateTime.now())));


        assertException(
                () -> service.validateAndIssuance(userId, resfreshToken),
                FailedAuthenticationException.class,
                DomainError.of(UserErrorType.Invalid_Authentication)
        );
    }


    @Test
    void validateAndIssuance_ShouldReturnToken_WhenNotSavedRefreshToken() {
        Identity userId = TestUserDomainGenerator.randomUserId();
        String resfreshToken = "ref";

        when(repository.findBy(userId))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.validateAndIssuance(userId, resfreshToken),
                FailedAuthenticationException.class,
                DomainError.of(UserErrorType.Invalid_Authentication)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}