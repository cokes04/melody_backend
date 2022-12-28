package com.melody.melody.application.service.user;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BusinessRuleChecker;
import com.melody.melody.domain.rule.PasswordMatches;
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
        User user = repository.findById(Identity.from(command.getUserId()))
                .orElseThrow(() -> new NotFoundException(DomainError.of(UserErrorType.User_Not_Found)));

        User.Password newPassword = passwordEncrypter.encrypt(command.getNewPassword());

        this.checkRule(
                PasswordMatches.create(passwordEncrypter, command.getOldPassword(), user.getPassword())
        );

        user.changePassword(newPassword);

        return new Result(repository.save(user));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final long userId;
        private final String oldPassword;
        private final String newPassword;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final User user;
    }
}
