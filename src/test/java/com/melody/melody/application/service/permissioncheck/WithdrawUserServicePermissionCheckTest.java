package com.melody.melody.application.service.permissioncheck;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.application.service.user.WithdrawUserService;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WithdrawUserServicePermissionCheckTest {

    @Autowired
    private WithdrawUserService service;

    @MockBean
    private UserRepository repository;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() {
        User user = TestUserDomainGenerator.randomUser(new User.UserId(requesterId));
        User.UserId userId = user.getId().get();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        WithdrawUserService.Command command = new WithdrawUserService.Command(userId);
        service.execute(command);

        verify(repository, times(1))
                .findById(userId);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf() {
        User user = TestUserDomainGenerator.randomUser(new User.UserId(53245 / 3L));
        User.UserId userId = user.getId().get();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        WithdrawUserService.Command command = new WithdrawUserService.Command(userId);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, times(0))
                .findById(any(User.UserId.class));
    }
}