package com.melody.melody.application.handler;

import com.melody.melody.application.port.in.EventHandler;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.event.MusicComposed;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicComposedHandler implements EventHandler<MusicComposed> {

    private final MusicRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handle(MusicComposed event) {
        Music music = repository.getById(new Music.MusicId(event.getMusicId()))
                .orElseThrow(() -> new NotFoundException(DomainError.of(MusicErrorType.Not_Found_Music)));

        music.completeGeneration(new Music.MusicUrl(event.getMusicUrl()));

        repository.save(music);
    }
}
