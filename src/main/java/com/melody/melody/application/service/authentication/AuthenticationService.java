package com.melody.melody.application.service.authentication;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BusinessRuleChecker;
import com.melody.melody.domain.rule.PasswordMatches;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthenticationService implements UseCase<AuthenticationService.Command, AuthenticationService.Result>, BusinessRuleChecker {
    private final UserRepository repository;
    private final PasswordEncrypter passwordEncrypter;

    @Override
    public Result execute(Command command) {

        User user = repository.findByEmail(command.email)
                .orElseThrow(
                        () -> new FailedAuthenticationException(DomainError.of(UserErrorType.Authentication_Failed))
                );

        this.checkRule(
                new PasswordMatches(passwordEncrypter, command.password, user.getPassword()),
                () -> new FailedAuthenticationException(DomainError.of(UserErrorType.Authentication_Failed))
        );

        return new Result(user);
    }

    @Value
    public static class Command implements UseCase.Command{
        private final String email;
        private final String password;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final User user;
    }
}
