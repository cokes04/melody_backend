package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Music;

import java.util.Optional;

public interface MusicRepository {
    Music save(Music music);

    Optional<Music> getById(Music.MusicId id);
}
