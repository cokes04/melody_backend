package com.melody.melody.adapter.persistence.post;

import com.melody.melody.application.dto.Open;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

public class PostQuerySupport {

    @Nullable
    public static BooleanExpression inId(@Nullable List<Long> userId){
        return Objects.isNull(userId) ? null : postEntity.id.in(userId);
    }

    @Nullable
    public static BooleanExpression eqDeleted(@Nullable Boolean deleted){
        return Objects.isNull(deleted) ? null : postEntity.deleted.eq(deleted);
    }

    @Nullable
    public static BooleanExpression eqOpen(@Nullable Open open){
        if (Objects.isNull(open)) return null;

        switch (open){
            case OnlyOpen:
                return postEntity.open.eq(true);
            case OnlyClose:
                return postEntity.open.eq(false);
            default:
                return null;
        }
    }

    @Nullable
    public static BooleanExpression eqUserId(@Nullable Long userId){
        if (userId == null) return null;
        return postEntity.userEntity.id.eq(userId);
    }

    @Nullable
    public static BooleanExpression nextPostId(Long postId, OrderSpecifier orderSpecifier){
        return orderSpecifier.isAscending() ?  gtPostId(postId) : ltPostId(postId);
    }

    @Nullable
    public static BooleanExpression startPostId(Long postId, OrderSpecifier orderSpecifier){
        return orderSpecifier.isAscending() ?  goePostId(postId) : loePostId(postId);
    }

    @Nullable
    public static BooleanExpression gtPostId(@Nullable Long postId){
        return Objects.isNull(postId) ? null : postEntity.id.gt(postId);
    }

    @Nullable
    public static BooleanExpression ltPostId(@Nullable Long postId){
        return Objects.isNull(postId) ? null : postEntity.id.lt(postId);
    }

    @Nullable
    public static BooleanExpression goePostId(@Nullable Long postId){
        return Objects.isNull(postId) ? null : postEntity.id.goe(postId);
    }

    @Nullable
    public static BooleanExpression loePostId(@Nullable Long postId){
        return Objects.isNull(postId) ? null : postEntity.id.loe(postId);
    }
}

