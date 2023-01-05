package com.melody.melody.application.service.permissioncheck.user;

import com.melody.melody.adapter.security.*;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.application.service.user.UpdateUserService;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = {
        UpdateUserService.class,
        CustomMethodSecurityConfig.class,
        CustomMethodSecurityExpressionHandler.class,
        UserSecurityExpression.class
})
public class UpdateUserServicePermissionCheckTest {

    @Autowired
    private UpdateUserService service;

    @MockBean
    private UserRepository repository;

    @MockBean
    private MusicSecurityExpression musicSecurityExpression;

    @MockBean
    private PostSecurityExpression postSecurityExpression;

    private final long requesterId = 53245;

    @Test
    @WithMockRequester(userId = requesterId)
    void excute_ShouldPass_WhenUserSelf() {
        User user = TestUserDomainGenerator.randomUser(Identity.from(requesterId));
        Identity userId = user.getId();

        when(repository.findById(userId))
                .thenReturn(Optional.of(user));

        UpdateUserService.Command command = new UpdateUserService.Command(userId.getValue(), "ASKLJF123");
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

        UpdateUserService.Command command = new UpdateUserService.Command(userId.getValue(), "ASKLJF123");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, times(0))
                .findById(any(Identity.class));
    }
}
