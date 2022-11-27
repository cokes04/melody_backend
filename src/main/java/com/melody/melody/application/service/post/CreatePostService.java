package com.melody.melody.application.service.post;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePostService implements UseCase<CreatePostService.Command, CreatePostService.Result> {
    private final PostRepository repository;

    @PreAuthorize("#music.isOwner(#command.musicId)")
    @Override
    public Result execute(Command command) {
        Post post = Post.create(
                command.userId,
                command.musicId,
                command.title,
                command.content,
                command.open
        );

        post = repository.save(post);
        return new Result(post);
    }

    @Value
    public static class Command implements UseCase.Command {
        private final User.UserId userId;
        private final Music.MusicId musicId;

        private final String title;
        private final String content;
        private final boolean open;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Post post;
    }
}
