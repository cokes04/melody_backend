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

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdatePostService implements UseCase<UpdatePostService.Command, UpdatePostService.Result> {
    private final PostRepository repository;

    @PreAuthorize("#post.isOwner(#command.postId)")
    @Override
    public Result execute(Command command) {
        Post post = repository.findById(Identity.from(command.postId))
                .orElseThrow(() -> new NotFoundException(DomainError.of(PostErrorType.Not_Found_Post)));

        String title = command.getTitle().isPresent() ? command.getTitle().get() : post.getTitle().getValue();
        String content = command.getContent().isPresent() ? command.getContent().get() : post.getContent().getValue();

        post.update(title, content);

        command.open.ifPresent(
                o -> {
                    if (o) post.open();
                    else post.close();
                }
        );

        return new Result(repository.save(post));
    }

    @Value
    public static class Command implements UseCase.Command {
        private final long postId;

        private final Optional<String> title;
        private final Optional<String> content;
        private final Optional<Boolean> open;

        public Command(long postId, String title, String content, Boolean open) {
            this.postId = postId;
            this.title = Optional.ofNullable(title);
            this.content = Optional.ofNullable(content);
            this.open = Optional.ofNullable(open);
        }
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Post post;
    }
}
