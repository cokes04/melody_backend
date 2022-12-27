package com.melody.melody.application.handler;

import com.melody.melody.application.port.in.EventHandler;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.event.MusicComposed;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MusicComposedEventHandler implements EventHandler<MusicComposed> {
    private final MusicRepository repository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = MusicComposed.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handle(MusicComposed event) {
        Music music = repository.findById(Identity.from(event.getMusicId()))
                .orElseThrow(() -> new NotFoundException(DomainError.of(MusicErrorType.Not_Found_Music)));

        music.completeGeneration(new Music.MusicUrl(event.getMusicUrl()));

        repository.save(music);
    }
}
