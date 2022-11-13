package com.melody.melody.application.service.music;

import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompleteGenerationMusicService implements UseCase<CompleteGenerationMusicService.Command, CompleteGenerationMusicService.Result > {

    private final MusicRepository repository;
    
    @Override
    public Result execute(Command command) {
        Music music = repository.getById(command.getId())
                .orElseThrow(() -> new NotFoundException(DomainError.of(MusicErrorType.Not_Found_Music)));

        music.completeGeneration(command.getMusicUrl());

        return new Result(music);
    }

    @Value
    public static class Command implements UseCase.Command {
        private final Music.MusicId id;
        private final Music.MusicUrl musicUrl;

    }

    @Value
    public static class Result implements UseCase.Result {
        private final Music music;
    }
}
