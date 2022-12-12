package com.melody.melody.application.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melody.melody.adapter.aop.PublishEvent;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.event.UserWithdrew;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
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
public class WithdrawUserService implements UseCase<WithdrawUserService.Command, WithdrawUserService.Result>, BusinessRuleChecker {
    private final UserRepository repository;

    @PreAuthorize("#user.isMe(#command.userId)")
    @Override
    public Result execute(Command command) {
        User user = repository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException(DomainError.of(UserErrorType.User_Not_Found)));

        user.withdraw();

        return new Result(repository.save(user));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final User.UserId userId;
    }

    @Value
    public static class Result implements UseCase.Result{
        private final User user;
    }
}
