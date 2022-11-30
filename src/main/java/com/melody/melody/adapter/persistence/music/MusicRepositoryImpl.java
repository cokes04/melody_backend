package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.MusicJpaRepository;
import com.melody.melody.adapter.persistence.music.MusicMapper;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class MusicRepositoryImpl implements MusicRepository {
    private final MusicJpaRepository jpaRepository;
    private final MusicMapper mapper;

    @Override
    public Music save(Music music) {
        MusicEntity entity = mapper.toEntity(music);
        entity = jpaRepository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Optional<Music> getById(Music.MusicId musicId) {
        Optional<MusicEntity> optional = jpaRepository.findById(musicId.getValue());

        if (optional.isEmpty())
            return Optional.empty();

        return optional
                .filter(m -> !Music.Status.DELETED.equals(m.getStatus()))
                .map(mapper::toModel);
    }
}
