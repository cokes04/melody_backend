package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPostService implements UseCase<GetPostService.Command, GetPostService.Result> {
    private final PostDetailRepository repository;

    @PostAuthorize("#post.isOwner(returnObject.postDetail) or #post.isOpen(returnObject.postDetail)")
    @Override
    public Result execute(Command command) {
        PostDetail postDetail = repository.findById(command.postId)
                .orElseThrow( () -> new NotFoundException(DomainError.of(PostErrorType.Not_Found_Post)));

         return new Result(postDetail);
    }

    @Value
    public static class Command implements UseCase.Command {
        private final Post.PostId postId;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final PostDetail postDetail;
    }
}
