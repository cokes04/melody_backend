package com.melody.melody.adapter.persistence.post;

import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum  PostOrderBy {
    ASC_createdDate(PostSort.newest, new OrderSpecifier(Order.ASC, Expressions.path(DateTimePath.class, QPostEntity.postEntity, "createdDate"))),
    DESC_createdDate(PostSort.oldest, new OrderSpecifier(Order.DESC, Expressions.path(DateTimePath.class, QPostEntity.postEntity, "createdDate")));

    private final PostSort postSort;
    private final OrderSpecifier orderSpecifier;

    public static Optional<PostOrderBy> get(PostSort postSort){
        for (PostOrderBy postOrderBy : PostOrderBy.values()){
            if (postOrderBy.postSort.equals(postSort))
                return Optional.of(postOrderBy);
        }
        return Optional.empty();
    }

    public static PostOrderBy getOrElseThrow(PostSort postSort){
        return get(postSort)
                .orElseThrow( () -> new InvalidArgumentException(DomainError.of(PostErrorType.Invalid_Post_Sort)));
    }
}
