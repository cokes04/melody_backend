package com.melody.melody.application.service.music;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMusicService implements UseCase<GetMusicService.Command, GetMusicService.Result> {
    private final MusicRepository musicRepository;

    @Override
    public Result execute(Command command) {
        return musicRepository.getById(command.musicId)
                .map(Result::new)
                .orElseThrow(() -> new NotFoundException(DomainError.of(MusicErrorType.Not_Found_Music)));
    }

    @Value
    public static class Command implements UseCase.Command {
        private final Music.MusicId musicId;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Music music;

    }
}
