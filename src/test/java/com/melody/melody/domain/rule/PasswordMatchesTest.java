package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        User.Password encrypterd = new User.Password("randomEncrypterdPassword");

        when(encrypter.matches(eq(raw), eq(encrypterd)))
                .thenReturn(true);

        PasswordMatches.create(encrypter, raw, encrypterd).check();
    }

    @Test
    void check_ShouldThrowException_WhenNotMatches() {
        String raw = "randomRawPassword";
        User.Password encrypted = new User.Password("randomEncrypterdPassword");

        when(encrypter.matches(eq(raw), eq(encrypted)))
                .thenReturn(false);

        assertException(
                () -> PasswordMatches.create(encrypter, raw, encrypted).check()
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