package com.melody.melody.application.service.user;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BreakBusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.melody.melody.application.service.user.TestUserServiceGenerator.randomCreateUserCommand;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CreateUserServiceTest {

    @InjectMocks
    private CreateUserService createUserService;
    @Mock private UserRepository repository;
    @Mock private PasswordEncrypter passwordEncrypter;


    @Test
    void execute_ShouldCreateAndReturnMusic() {
        CreateUserService.Command command = randomCreateUserCommand();
        User user = TestUserDomainGenerator.randomUser();

        when(repository.existsByEmail(eq(command.getEmail())))
                .thenReturn(false);

        when(repository.save(any(User.class)))
                .thenReturn(user);

        when(passwordEncrypter.encrypt(eq(command.getPassword())))
                .thenReturn(user.getPassword());

        CreateUserService.Result result = createUserService.execute(command);

        User actualUser = result.getUser();
        assertEquals(user, actualUser);

        verify(repository, times(1)).save(any(User.class));
        verify(repository, times(1)).existsByEmail(eq(command.getEmail()));
        verify(passwordEncrypter, times(1)).encrypt(eq(command.getPassword()));
    }

    @Test
    void execute_ThrowException_WhenExistEmail() {
        CreateUserService.Command command = randomCreateUserCommand();
        User user = TestUserDomainGenerator.randomUser();

        when(repository.existsByEmail(eq(command.getEmail())))
                .thenReturn(true);

        assertException(
                () -> createUserService.execute(command),
                BreakBusinessRuleException.class,
                DomainError.of(UserErrorType.Email_Already_Used)
        );

        verify(repository, times(0)).save(any(User.class));
    }

    private void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}