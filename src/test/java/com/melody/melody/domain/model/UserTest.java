package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.UserErrorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static com.melody.melody.domain.model.TestUserDomainGenerator.*;

class UserTest {

    @Test
    void create() {
        String lastName = randomLastName();
        String firstName =randomFirstName();
        String email =randomEmail();
        Password password =randomPassword();

        User actual = User.create(
                lastName,
                firstName,
                email,
                password
        );

        assertTrue(actual.getId().isEmpty());
        assertEquals(lastName, actual.getLastName());
        assertEquals(firstName, actual.getFirstName());
        assertEquals(email, actual.getEmail());
        assertEquals(password, actual.getPassword());
    }

    @Test
    void withdaw_ShuoldChangeWithdawnStatus() {
        User user = TestUserDomainGenerator.randomUser();

        assertFalse(user.isWithdrawn());
        user.withdraw();
        assertTrue(user.isWithdrawn());
    }

    @Test
    void withdaw_ShouldException_WhenWithdawn() {
        User user = TestUserDomainGenerator.randomUser();
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