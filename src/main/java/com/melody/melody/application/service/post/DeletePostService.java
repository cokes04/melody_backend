package com.melody.melody.application.service.post;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePostService implements UseCase<DeletePostService.Command, DeletePostService.Result> {
    private final PostRepository repository;

    @PreAuthorize("#post.isOwner(#command.postId)")
    @Override
    public Result execute(Command command) {
        Post post = repository.findById(Identity.from(command.getPostId()))
                .orElseThrow(() -> new NotFoundException(DomainError.of(PostErrorType.Not_Found_Post)));

        post.delete();

        return new Result(repository.save(post));
    }

    @Value
    public static class Command implements UseCase.Command {
        private final long postId;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Post post;
    }
}
