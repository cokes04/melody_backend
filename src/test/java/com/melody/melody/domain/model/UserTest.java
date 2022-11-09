package com.melody.melody.domain.model;

import org.junit.jupiter.api.Test;

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
}