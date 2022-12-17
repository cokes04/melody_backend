package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.melody.melody.adapter.persistence.music.QMusicEntity.*;
import static com.melody.melody.adapter.persistence.user.QUserEntity.*;
import static com.melody.melody.adapter.persistence.post.QPostEntity.*;

@PersistenceAdapter
@RequiredArgsConstructor
public class MusicRepositoryImpl implements MusicRepository {
    private final MusicJpaRepository jpaRepository;
    private final JPAQueryFactory factory;
    private final MusicMapper mapper;

    @Override
    public Music save(Music music) {
        MusicEntity entity = mapper.toEntity(music);
        entity = jpaRepository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Optional<Music> findById(Music.MusicId musicId) {
        MusicData result = select()
                .where(musicEntity.id.eq(musicId.getValue()))
                .fetchOne();

        return Optional.ofNullable(result)
                .filter(m -> !Music.Status.DELETED.equals(m.getStatus()))
                .map(mapper::toModel);
    }

    @Override
    public PagingResult<Music> findByUserId(User.UserId userId, MusicPublish musicPublish, PagingInfo<MusicSort> musicPaging) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(musicEntity.status.ne(Music.Status.DELETED));
        where.and(musicEntity.userId.eq(userId.getValue()));

        JPAQuery<MusicData> query = select();

        List<Music> result = query
                .where(where)
                .orderBy(MusicOrderBy
                        .get(musicPaging.getSorting())
                        .map(MusicOrderBy::getOrderSpecifier)
                        .orElseThrow(() -> new InvalidArgumentException(DomainError.of(MusicErrorType.Invailid_Music_Sort)))
                )
                .offset(musicPaging.getPage() * musicPaging.getSize())
                .limit(musicPaging.getSize())
                .fetch()
                .stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());

        int totalSize = select().where(where).fetch().size();

        return  new PagingResult<Music>(result, result.size(), totalSize, (int)Math.ceil((double) totalSize / musicPaging.getSize()));
    }

    public void deleteByUserId(User.UserId userId){
        factory.update(musicEntity)
                .set(musicEntity.status, Music.Status.DELETED)
                .where(musicEntity.userId.eq(userId.getValue()))
                .execute();
    }

    private JPAQuery<MusicData> select(){
        return factory.select(
                new QMusicData(
                        musicEntity.id,
                        musicEntity.userId,
                        musicEntity.emotion,
                        musicEntity.explanation,
                        musicEntity.imageUrl,
                        musicEntity.musicUrl,
                        musicEntity.status
                )
        )
                .from(musicEntity);
    }
}
