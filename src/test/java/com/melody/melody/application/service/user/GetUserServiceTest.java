package com.melody.melody.application.service.user;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetUserServiceTest {
    private GetUserService service;
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        service = new GetUserService(repository);
    }

    @Test
    void excute_ShouldReturnUser_WhenExistUser() {
        User user = TestUserDomainGenerator.randomUser();
        Identity userId = user.getId();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        GetUserService.Command command = new GetUserService.Command(userId.getValue());
        GetUserService.Result result = service.execute(command);
        User actual = result.getUser();

        assertEquals(user, actual);
    }

    @Test
    void excute_ShouldThrowException_WhenNotExsitUser() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        when(repository.findById(userId))
                .thenReturn(Optional.empty());

        GetUserService.Command command = new GetUserService.Command(userId.getValue());
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