package com.melody.melody.application.service.music;

import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserMusicService implements UseCase<GetUserMusicService.Command, GetUserMusicService.Result> {
    private final MusicRepository musicRepository;

    @PreAuthorize("#user.isMe(#command.userId)")
    @Override
    public Result execute(Command command) {
        PagingResult<Music> pagingResult = musicRepository.findByUserId(Identity.from(command.userId), command.getMusicPublishing(), command.getMusicPaging());

        return new Result(pagingResult);
    }

    @Value
    public static class Command implements UseCase.Command {
        private final long userId;
        private final MusicPublish musicPublishing;
        private final PagingInfo<MusicSort> musicPaging;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final PagingResult<Music> pagingResult;

    }
}
