package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.melody.melody.domain.model.TestUserDomainGenerator.randomEmail;

class EmailIsUniqueTest {
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
    }

    @Test
    void check() {
        User.Email email = randomEmail();

        when(repository.existsByEmail(email))
                .thenReturn(false);

        new EmailIsUnique(repository, email).check();

        verify(repository, times(1))
                .existsByEmail(email);
    }

    @Test
    void check_ShouldThrowException_WhenNotUniqueEmail() {
        User.Email email = randomEmail();

        when(repository.existsByEmail(email))
                .thenReturn(true);

        assertException(
                () -> new EmailIsUnique(repository, email).check(),
                BreakBusinessRuleException.class,
                DomainError.of(UserErrorType.Email_Already_Used)
        );

        verify(repository, times(1))
                .existsByEmail(email);
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}