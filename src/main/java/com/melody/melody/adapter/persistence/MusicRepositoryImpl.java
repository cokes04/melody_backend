package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
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
    public Optional<Music> getById(Music.MusicId id) {
        Optional<MusicEntity> optional = jpaRepository.findById(id.getValue());

        if (optional.isEmpty())
            return Optional.empty();

        return Optional.of(mapper.toModel(optional.get()));
    }
}
