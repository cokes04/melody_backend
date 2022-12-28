package com.melody.melody.adapter.persistence.post;

import com.melody.melody.application.dto.Open;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.jetbrains.annotations.Nullable;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

public class PostQuerySupport {

    @Nullable
    public static BooleanExpression eqOpen(Open open){
        switch (open){
            case OnlyOpen:
                return postEntity.open.eq(true);
            case OnlyClose:
                return postEntity.open.eq(false);
            default:
                return null;
        }
    }
}
