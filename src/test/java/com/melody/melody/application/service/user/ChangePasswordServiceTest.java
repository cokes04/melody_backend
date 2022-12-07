package com.melody.melody.application.service.user;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Password;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BreakBusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChangePasswordServiceTest {
    private ChangePasswordService service;
    private UserRepository repository;
    private PasswordEncrypter encrypter;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        encrypter = Mockito.mock(PasswordEncrypter.class);

        service = new ChangePasswordService(repository, encrypter);
    }

    @Test
    void excute_ShouldReturnPasswordChangedUser() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        String oldRawPassowrd = "sadlkweqkjwasd";
        String newRawPassword = "fseop4ir2fk20349";
        Password newEncryptedPassowrd = TestUserDomainGenerator.randomPassword();
        ChangePasswordService.Command command = new ChangePasswordService.Command(
                userId,
                oldRawPassowrd,
                newRawPassword
        );


        when(repository.save(any(User.class)))
                .thenAnswer(a -> a.getArgument(0, User.class));

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        when(encrypter.encrypt(newRawPassword))
                .thenReturn(newEncryptedPassowrd);

        when(encrypter.matches(oldRawPassowrd, user.getPassword()))
                .thenReturn(true);

        ChangePasswordService.Result result = service.execute(command);

        assertEquals(newEncryptedPassowrd, result.getUser().getPassword());
    }

    @Test
    void excute_ShouldException_WhenNotFoundUser() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        String oldRawPassowrd = "sadlkweqkjwasd";
        String newRawPassword = "fseop4ir2fk20349";
        ChangePasswordService.Command command = new ChangePasswordService.Command(
                userId,
                oldRawPassowrd,
                newRawPassword
        );

        when(repository.findById(userId))
                .thenReturn(Optional.empty());


        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(UserErrorType.User_Not_Found)
        );
    }

    @Test
    void excute_ShouldException_WhenNotMatchedPassword() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        String oldRawPassowrd = "sadlkweqkjwasd";
        String newRawPassword = "fseop4ir2fk20349";
        Password newEncryptedPassowrd = TestUserDomainGenerator.randomPassword();
        ChangePasswordService.Command command = new ChangePasswordService.Command(
                userId,
                oldRawPassowrd,
                newRawPassword
        );

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        when(encrypter.encrypt(newRawPassword))
                .thenReturn(newEncryptedPassowrd);

        when(encrypter.matches(oldRawPassowrd, user.getPassword()))
                .thenReturn(false);


        assertException(
                () -> service.execute(command),
                BreakBusinessRuleException.class,
                DomainError.of(UserErrorType.Passwod_Not_Matches)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}