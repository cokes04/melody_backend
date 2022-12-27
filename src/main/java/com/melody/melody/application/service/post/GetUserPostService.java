package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserPostService implements UseCase<GetUserPostService.Command, GetUserPostService.Result> {
    private final PostDetailRepository repository;

    @PreAuthorize("#user.isMe(#command.userId) or #post.isOpen(#command.open)")
    @Override
    public Result execute(Command command) {
        PagingResult<PostDetail> pagingResult = repository.findByUserId(Identity.from(command.getUserId()), command.getOpen(), command.getPostPaging());

        return new Result(pagingResult);
    }

    @Value
    public static class Command implements UseCase.Command {
        private final long userId;
        private final Open open;
        private final PagingInfo<PostSort> postPaging;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final PagingResult<PostDetail> pagingResult;
    }
}
