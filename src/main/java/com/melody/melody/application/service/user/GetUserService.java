package com.melody.melody.application.service.user;

import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.dto.SearchedUser;
import com.melody.melody.application.dto.UserSort;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.SearchedUserRepository;
import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserService implements UseCase<GetUserService.Command, GetUserService.Result> {
    private final UserRepository repository;

    @PreAuthorize("#user.isMe(#command.userId)")
    @Override
    public Result execute(Command command) {
        return repository.findById(command.userId)
                .map(Result::new)
                .orElseThrow(() -> new NotFoundException(DomainError.of(UserErrorType.User_Not_Found)));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final User.UserId userId;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final User user;
    }
}
