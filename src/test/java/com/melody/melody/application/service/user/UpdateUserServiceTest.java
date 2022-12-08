package com.melody.melody.application.service.user;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UpdateUserServiceTest {
    private UpdateUserService service;
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        service = new UpdateUserService(repository);
    }
    @Test
    void excute_ShouldReturnUpdatedUser() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        String newNickName = TestUserDomainGenerator.randomNickName().getValue();
        UpdateUserService.Command command = new UpdateUserService.Command(userId, newNickName);

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        when(repository.save(any(User.class)))
                .thenAnswer( e -> e.getArgument(0, User.class));


        UpdateUserService.Result result = service.execute(command);

        assertEquals(newNickName, result.getUser().getNickName().getValue());
    }

    @Test
    void excute_ShouldException_WhenNotFoundUser() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        String newNickName = TestUserDomainGenerator.randomNickName().getValue();
        UpdateUserService.Command command = new UpdateUserService.Command(userId, newNickName);

        when(repository.findById(userId))
                .thenReturn(Optional.empty());

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