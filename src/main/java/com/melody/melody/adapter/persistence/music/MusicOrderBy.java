package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.post.QPostEntity;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PostSort;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum MusicOrderBy {
    ASC_createdDate(MusicSort.newest, new OrderSpecifier(Order.ASC, Expressions.path(DateTimePath.class, QMusicEntity.musicEntity, "createdDate"))),
    DESC_createdDate(MusicSort.oldest, new OrderSpecifier(Order.DESC, Expressions.path(DateTimePath.class, QMusicEntity.musicEntity, "createdDate")));

    private final MusicSort musicSort;
    private final OrderSpecifier orderSpecifier;

    public static Optional<MusicOrderBy> get(MusicSort musicSort){
        for (MusicOrderBy musicOrderBy : MusicOrderBy.values()){
            if (musicOrderBy.musicSort.equals(musicSort))
                return Optional.of(musicOrderBy);
        }
        return Optional.empty();
    }

}
