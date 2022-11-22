package com.melody.melody.application.service.user;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WithdrawUserServiceTest {
    private WithdrawUserService service;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        service = new WithdrawUserService(userRepository);
    }

    @Test
    void execute_ShouldReturnUser() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId id = user.getId().get();

        when(userRepository.findById(eq(id)))
                .thenReturn(Optional.of(user));

        WithdrawUserService.Command command = new WithdrawUserService.Command(id);

        WithdrawUserService.Result actual = service.execute(command);
        User actualUser = actual.getUser();

        assertEquals(user, actualUser);
    }

    @Test
    void execute_ShouldException_WhenNotFoundUser() {
        User.UserId id = TestUserDomainGenerator.randomUserId();

        when(userRepository.findById(eq(id)))
                .thenReturn(Optional.empty());

        WithdrawUserService.Command command = new WithdrawUserService.Command(id);

        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(UserErrorType.User_Not_Found)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}