package com.melody.melody.adapter.persistence.post.size;

import com.melody.melody.adapter.persistence.post.PostQuerySupport;
import com.melody.melody.application.dto.Open;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;


@Repository
@RequiredArgsConstructor
public class PostSizeDao {
    private final JPAQueryFactory factory;

    @Cacheable(cacheNames = "userPostSize",
            key = "#userId.value + #sizeInfo.symbol",
            condition = "#sizeInfo != null and #userId != null")
    public long findSize(Identity userId, SizeInfo sizeInfo){
        return this.findSize(userId, sizeInfo.getOpen());
    }

    public long findSize(Identity userId, Open open){
        BooleanBuilder where = new BooleanBuilder();
        where.and(PostQuerySupport.goePostId(0L));
        where.and(PostQuerySupport.eqDeleted(false));
        where.and(PostQuerySupport.eqOpen(open));
        where.and(PostQuerySupport.eqUserId(userId.getValue()));

        Long result = factory
                .select(postEntity.count())
                .from(postEntity)
                .where(where)
                .fetchOne();

        return result == null ? 0 : result;
    }

}
