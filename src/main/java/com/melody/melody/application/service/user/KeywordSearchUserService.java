package com.melody.melody.application.service.user;

import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.dto.SearchedUser;
import com.melody.melody.application.dto.UserSort;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.SearchedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordSearchUserService implements UseCase<KeywordSearchUserService.Command, KeywordSearchUserService.Result> {
    private final SearchedUserRepository repository;

    @Override
    public Result execute(Command command) {
        return new Result(repository.search(command.keyword, command.pagingInfo));
    }

    @Value
    public static class Command implements UseCase.Command{
        private final String keyword;
        private final PagingInfo<UserSort> pagingInfo;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final PagingResult<SearchedUser> pagingResult;
    }
}
