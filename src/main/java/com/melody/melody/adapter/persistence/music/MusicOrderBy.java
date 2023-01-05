package com.melody.melody.adapter.persistence.music;

import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum MusicOrderBy {
    ASC_createdDate(MusicSort.newest, new OrderSpecifier(Order.ASC, Expressions.path(NumberPath.class, QMusicEntity.musicEntity, "id"))),
    DESC_createdDate(MusicSort.oldest, new OrderSpecifier(Order.DESC, Expressions.path(NumberPath.class, QMusicEntity.musicEntity, "id")));

    private final MusicSort musicSort;
    private final OrderSpecifier orderSpecifier;

    public static Optional<MusicOrderBy> get(MusicSort musicSort){
        for (MusicOrderBy musicOrderBy : MusicOrderBy.values()){
            if (musicOrderBy.musicSort.equals(musicSort))
                return Optional.of(musicOrderBy);
        }
        return Optional.empty();
    }

    public static MusicOrderBy getOrElseThrow(MusicSort musicSort){
        return get(musicSort)
                .orElseThrow( () -> new InvalidArgumentException(DomainError.of(MusicErrorType.Invalid_Music_Sort)));
    }
}
