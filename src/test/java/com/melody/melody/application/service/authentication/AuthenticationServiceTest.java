package com.melody.melody.application.service.authentication;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.application.service.user.TestUserServiceGenerator;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceTest {
    @InjectMocks private AuthenticationService service;

    @Mock private UserRepository repository;
    @Mock private PasswordEncrypter passwordEncrypter;

    @Test
    void execute_ShouldReturnUser() {
        String email = TestUserServiceGenerator.randomEmail();
        String password = TestUserServiceGenerator.randomPassword();
        User user = TestUserDomainGenerator.randomUser();

        AuthenticationService.Command command = new AuthenticationService.Command(email, password);

        when(repository.findByEmail(eq(email)))
                .thenReturn(Optional.of(user));

        when(passwordEncrypter.matches(eq(password), eq(user.getPassword())))
                .thenReturn(true);

        AuthenticationService.Result actual = service.execute(command);

        assertEquals(user, actual.getUser());

    }
    @Test
    void execute_ShouldThrowException_WhenNotFoundEmail() {
        String email = TestUserServiceGenerator.randomEmail();
        String password = TestUserServiceGenerator.randomPassword();

        AuthenticationService.Command command = new AuthenticationService.Command(email, password);

        when(repository.findByEmail(eq(email)))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.execute(command),
                FailedAuthenticationException.class,
                DomainError.of(UserErrorType.Authentication_Failed)
        );
    }

    @Test
    void execute_ShouldThrowException_WhenMismatchedPassword() {
        String email = TestUserServiceGenerator.randomEmail();
        String password = TestUserServiceGenerator.randomPassword();
        User user = TestUserDomainGenerator.randomUser();

        AuthenticationService.Command command = new AuthenticationService.Command(email, password);

        when(repository.findByEmail(eq(email)))
                .thenReturn(Optional.of(user));

        when(passwordEncrypter.matches(eq(password), eq(user.getPassword())))
                .thenReturn(false);

        assertException(
                () -> service.execute(command),
                FailedAuthenticationException.class,
                DomainError.of(UserErrorType.Authentication_Failed)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}