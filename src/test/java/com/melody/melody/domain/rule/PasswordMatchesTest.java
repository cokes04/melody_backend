package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PasswordMatchesTest {

    private PasswordEncrypter encrypter;

    @BeforeEach
    void setUp() {
        encrypter = Mockito.mock(PasswordEncrypter.class);
    }

    @Test
    void check_ShouldPass() {
        String raw = "randomRawPassword";
        Password encrypterd = new Password("randomEncrypterdPassword");

        when(encrypter.matches(eq(raw), eq(encrypterd)))
                .thenReturn(true);

        new PasswordMatches(
                encrypter,
                raw,
                encrypterd
        ).check();
    }

    @Test
    void check_ShouldThrowException_WhenNotMatches() {
        String raw = "randomRawPassword";
        Password encrypted = new Password("randomEncrypterdPassword");

        when(encrypter.matches(eq(raw), eq(encrypted)))
                .thenReturn(false);

        assertException(
                () -> new PasswordMatches(
                        encrypter,
                        raw,
                        encrypted
                ).check()
                , BreakBusinessRuleException.class,
                DomainError.of(UserErrorType.Passwod_Not_Matches)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}