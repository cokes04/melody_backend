package com.melody.melody.application.service.permissioncheck.user;

import com.melody.melody.adapter.security.WithMockRequester;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.application.service.user.ChangePasswordService;
import com.melody.melody.domain.model.Identity;
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
public class ChangePasswordServicePermissionCheckTest {

    @Autowired
    private ChangePasswordService service;

    @MockBean
    private UserRepository repository;

    @MockBean
    private PasswordEncrypter passwordEncrypter;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() {
        User user = TestUserDomainGenerator.randomUser(Identity.from(requesterId));
        Identity userId = user.getId();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncrypter.matches(any(String.class), any(User.Password.class)))
                .thenReturn(true);

        ChangePasswordService.Command command = new ChangePasswordService.Command(userId.getValue(), "ASkl412dcasi123!@#", "Saklj321@!jdka");
        service.execute(command);

        verify(repository, times(1))
                .findById(userId);
    }

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldBlock_WhenNotUserSelf() {
        User user = TestUserDomainGenerator.randomUser(Identity.from(requesterId / 3L));
        Identity userId = user.getId();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncrypter.matches(any(String.class), any(User.Password.class)))
                .thenReturn(true);

        ChangePasswordService.Command command = new ChangePasswordService.Command(userId.getValue(), "ASdjk2341d$@!", "Saklj321@!jdka");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, times(0))
                .findById(any(Identity.class));
    }
}
