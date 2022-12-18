package com.melody.melody.application.port.out;

import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;

import java.util.Optional;

public interface MusicRepository {
    Music save(Music music);

    Optional<Music> findById(Music.MusicId id);

    PagingResult<Music> findByUserId(String userId, MusicPublish musicPublish, PagingInfo<MusicSort> musicPaging);

    void deleteByUserId(String userId);
}
