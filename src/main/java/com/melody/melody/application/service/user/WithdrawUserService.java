package com.melody.melody.application.service.user;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import com.melody.melody.domain.rule.BusinessRuleChecker;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WithdrawUserService implements UseCase<WithdrawUserService.Command, WithdrawUserService.Result>, BusinessRuleChecker {
    private final UserRepository userRepository;

    @Override
    public Result execute(Command command) {
        User user = userRepository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException(DomainError.of(UserErrorType.User_Not_Found)));

        user.withdraw();

        return new Result(user);
    }

    @Value
    public static class Command implements UseCase.Command{
        private final User.UserId id;
    }

    @Value
    public static class Result implements UseCase.Result{
        private final User user;
    }
}
