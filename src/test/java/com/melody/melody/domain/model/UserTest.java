package com.melody.melody.domain.model;

import com.melody.melody.domain.event.Events;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.event.UserWithdrew;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.rule.BreakBusinessRuleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static com.melody.melody.domain.model.TestUserDomainGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserTest {

    private PasswordEncrypter encrypter;
    private MockedStatic<Events> eventsMockedStatic;


    @BeforeEach
    void setUp() {
        encrypter = Mockito.mock(PasswordEncrypter.class);
        eventsMockedStatic = Mockito.mockStatic(Events.class);
    }

    @AfterEach
    void tearDown() {
        eventsMockedStatic.close();
    }

    @Test
    void create_ShuoldReturnCreatedUser() {
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
    void update_ShouldThrowException_WhenWithdawn() {
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
    void changePassword_ShouldThrowException_WhenOldPasswordNotMatches() {
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
    void changePassword_ShouldThrowException_WhenWithdawn() {
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

        eventsMockedStatic.verify(() -> Events.raise(eq(new UserWithdrew(user.getId().get().getValue()))), times(1));
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