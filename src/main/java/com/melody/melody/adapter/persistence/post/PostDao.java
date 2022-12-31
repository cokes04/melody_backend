package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostDao {
    private final EntityManager em;
    private final PostJpaRepository jpaRepository;
    private final JPAQueryFactory factory;
    private final PostMapper mapper;

    @Caching(evict = {
            @CacheEvict(cacheNames = "userPostSize", key = "#post.userId.value + '_Open'", condition = "#post.userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#post.userId.value + '_Close'", condition = "#post.userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#post.userId.value + '_Deleted'", condition = "#post.userId != null")
    })
    public Post save(Post post) {
        PostEntity entity = mapper.toEntity(post);
        entity = jpaRepository.save(entity);
        return mapper.toModel(entity);
    }

    public Optional<Post> findById(Identity postId) {
        Optional<PostEntity> entity = jpaRepository.findById(postId.getValue());
        return entity.filter(e -> !e.isDeleted()).map(mapper::toModel);
    }
/*

    @PostPageStatisticCacheable(userId = "#userId", size = "#size", postSort = "#postSort")
    public List<PageStatistics> findPageStatistics(Identity userId, int size, PostSort postSort){
        OrderSpecifier orderSpecifier = PostOrderBy.get(postSort)
                .map(PostOrderBy::getOrderSpecifier)
                .orElseThrow(() -> new InvalidArgumentException(DomainError.of(PostErrorType.Invalid_Post_Sort)));

        BooleanBuilder where = new BooleanBuilder();
        where.and(postEntity.deleted.eq(false));
        where.and(postEntity.userEntity.id.eq(userId.getValue()));

        List<PageStatistic> result = selectPageStatistic()
                .where(where)
                .orderBy(orderSpecifier)
                .limit(size)
                .fetch();


        return null;
    }
*/

    @Cacheable(cacheNames = "userPostSize",
            key = "#userId.value + '_' + #cachedSizeInfo.name()",
            condition = "!T(com.melody.melody.application.dto.Open).Everything.equals(#open) and #cachedSizeInfo != null and #userId != null")
    public long findSize(Identity userId, Open open, SizeInfo cachedSizeInfo){
        return this.findSize(userId, open);
    }

    public long findSize(Identity userId, Open open){
        BooleanBuilder where = new BooleanBuilder();
        where.and(postEntity.deleted.eq(false));
        where.and(PostQuerySupport.eqOpen(open));
        where.and(postEntity.userEntity.id.eq(userId.getValue()));

        Long result = factory
                .select(postEntity.count())
                .from(postEntity)
                .where(where)
                .fetchOne();

        return result == null ? 0 : result;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "userPostSize", key = "#userId.value + '_Open'", condition = "#userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#userId.value + '_Close'", condition = "#userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#userId.value + '_Deleted'", condition = "#userId != null")
    })
    public void deleteByUserId(Identity userId){
        factory.update(postEntity)
                .set(postEntity.deleted, true)
                .where(postEntity.userEntity.id.eq(userId.getValue()))
                .execute();

        em.flush();
        em.clear();
    }

/*
    private JPAQuery<PageStatistic> selectPageStatistic(){
        return factory.select(
                new QPostDao_PageStatistic(
                        postEntity.id,
                        postEntity.open
                )
        )
                .from(postEntity);
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class PageStatistic{
        private long id;
        private boolean open;

        @QueryProjection
        public PageStatistic(long id, boolean open) {
            this.id = id;
            this.open = open;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class PageStatistics{
        private long lastPostId;
        private long open;
        private long close;
    }

*/

}
