package com.melody.melody.adapter.persistence.post;

import com.melody.melody.application.dto.PostSort;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  PostOrderBy {
    ASC_createdDate(PostSort.newest, new OrderSpecifier(Order.ASC, Expressions.path(DateTimePath.class, QPostEntity.postEntity, "createdDate"))),
    DESC_createdDate(PostSort.oldest, new OrderSpecifier(Order.DESC, Expressions.path(DateTimePath.class, QPostEntity.postEntity, "createdDate")));

    private final PostSort postSort;
    private final OrderSpecifier orderSpecifier;

    public static PostOrderBy get(PostSort postSort){
        for (PostOrderBy postOrderBy : PostOrderBy.values()){
            if (postOrderBy.postSort.equals(postSort))
                return postOrderBy;
        }
        return null;
    }

}
