package com.melody.melody.application.service.user;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Password;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BusinessRuleChecker;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChangePasswordService implements UseCase<ChangePasswordService.Command, ChangePasswordService.Result>, BusinessRuleChecker {
    private final UserRepository repository;
    private final PasswordEncrypter passwordEncrypter;

    @PreAuthorize("#user.isMe(#command.userId)")
    @Override
    public Result execute(Command command) {
        User user = repository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException(DomainError.of(UserErrorType.User_Not_Found)));

        Password newPassword = passwordEncrypter.encrypt(command.newPassword);
        user.changePassword(passwordEncrypter, command.oldPassword, newPassword);

        return new Result(repository.save(user));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final User.UserId userId;
        private final String oldPassword;
        private final String newPassword;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final User user;
    }
}
