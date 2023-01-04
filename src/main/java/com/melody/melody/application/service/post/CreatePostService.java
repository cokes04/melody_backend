package com.melody.melody.application.service.post;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Identity;
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

    @PreAuthorize("#music.isExist(#command.musicId) and #music.isOwner(#command.musicId) and #user.isMe(#command.userId)")
    @Override
    public Result execute(Command command) {
        Post post = Post.create(
                Identity.from(command.getUserId()),
                Identity.from(command.getMusicId()),
                command.getTitle(),
                command.getContent(),
                command.isOpen()
        );

        return new Result(repository.save(post));
    }

    @Value
    public static class Command implements UseCase.Command {
        private final long userId;
        private final long musicId;

        private final String title;
        private final String content;
        private final boolean open;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Post post;
    }
}
