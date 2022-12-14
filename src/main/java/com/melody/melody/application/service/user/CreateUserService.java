package com.melody.melody.application.service.user;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BusinessRuleChecker;
import com.melody.melody.domain.rule.EmailIsUnique;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService implements UseCase<CreateUserService.Command, CreateUserService.Result>, BusinessRuleChecker {
    private final UserRepository repository;
    private final PasswordEncrypter passwordEncrypter;

    @Override
    public Result execute(Command command) {

        this.checkRule(
                EmailIsUnique.create(repository, User.Email.from(command.getEmail()))
        );

        User.Password password = passwordEncrypter.encrypt(command.getPassword());

        User user = User.create(
                command.getNickName(),
                command.getEmail(),
                password
        );

        return new Result(repository.save(user));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final String nickName;
        private final String email;
        private final String password;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final User user;
    }
}
