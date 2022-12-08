package com.melody.melody.domain.model;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.rule.BreakBusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static com.melody.melody.domain.model.TestUserDomainGenerator.*;
import static org.mockito.Mockito.when;

class UserTest {

    private PasswordEncrypter encrypter;

    @BeforeEach
    void setUp() {
        encrypter = Mockito.mock(PasswordEncrypter.class);
    }

    @Test
    void create() {
        String nickName = randomNickName().getValue();
        String email =randomEmail().getValue();
        Password password =randomPassword();

        User actual = User.create(
                nickName,
                email,
                password
        );

        assertTrue(actual.getId().isEmpty());
        assertEquals(nickName, actual.getNickName().getValue());
        assertEquals(email, actual.getEmail().getValue());
        assertEquals(password, actual.getPassword());
    }

    @Test
    void update_ShuoldChangeNickName() {
        User user = randomUser();
        String nickName = randomNickName().getValue();

        user.update(nickName);

        assertEquals(nickName, user.getNickName().getValue());
    }

    @Test
    void update_ShouldException_WhenWithdawn() {
        User user = randomUser();
        user.withdraw();
        String nickName = randomNickName().getValue();


        assertTrue(user.isWithdrawn());

        assertException(
                () -> user.update(nickName),
                InvalidStatusException.class,
                DomainError.of(UserErrorType.User_Already_Withdawn_Status)
        );
    }

    @Test
    void changePassword_ShouldChangePassword_WhenOldPasswordMatches() {
        User user = randomUser();
        String oldRawPassword =  "abcdefg123EWAS";
        Password newPassword = randomPassword();

        when(encrypter.matches(oldRawPassword, user.getPassword()))
                .thenReturn(true);

        user.changePassword(encrypter, oldRawPassword, newPassword);

        assertEquals(user.getPassword(), newPassword);
    }

    @Test
    void changePassword_ShouldException_WhenOldPasswordNotMatches() {
        User user = randomUser();
        String oldRawPassword =  "abcdefg123EWAS";
        Password newPassword = randomPassword();

        when(encrypter.matches(oldRawPassword, user.getPassword()))
                .thenReturn(false);


        assertException(
                () -> user.changePassword(encrypter, oldRawPassword, newPassword),
                BreakBusinessRuleException.class,
                DomainError.of(UserErrorType.Passwod_Not_Matches)
        );
    }

    @Test
    void changePassword_ShouldException_WhenWithdawn() {
        User user = randomUser();
        user.withdraw();
        String oldRawPassword =  "abcdefg123EWAS";
        Password newPassword = randomPassword();

        assertTrue(user.isWithdrawn());

        assertException(
                () -> user.changePassword(encrypter, oldRawPassword, newPassword),
                InvalidStatusException.class,
                DomainError.of(UserErrorType.User_Already_Withdawn_Status)
        );
    }

    @Test
    void withdaw_ShuoldChangeWithdawnStatus() {
        User user = randomUser();

        assertFalse(user.isWithdrawn());
        user.withdraw();
        assertTrue(user.isWithdrawn());
    }

    @Test
    void withdaw_ShouldException_WhenWithdawn() {
        User user = randomUser();
        user.withdraw();
        assertTrue(user.isWithdrawn());

        assertException(
                user::withdraw,
                InvalidStatusException.class,
                DomainError.of(UserErrorType.User_Already_Withdawn_Status)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}