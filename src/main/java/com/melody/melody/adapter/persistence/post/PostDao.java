package com.melody.melody.adapter.persistence.post;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.melody.melody.adapter.persistence.post.QPostEntity.postEntity;

@Repository
@RequiredArgsConstructor
public class PostDao {
    private final EntityManager em;
    private final PostJpaRepository jpaRepository;
    private final JPAQueryFactory factory;
    private final PostMapper mapper;

    @Caching(evict = {
            @CacheEvict(cacheNames = "userPostSize", key = "#post.userId.value + 'O'", condition = "#post.userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#post.userId.value + 'C'", condition = "#post.userId != null"),
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

    @Caching(evict = {
            @CacheEvict(cacheNames = "userPostSize", key = "#userId.value + 'O'", condition = "#userId != null"),
            @CacheEvict(cacheNames = "userPostSize", key = "#userId.value + 'C'", condition = "#userId != null"),
    })
    public void deleteByUserId(Identity userId){
        factory.update(postEntity)
                .set(postEntity.deleted, true)
                .where(PostQuerySupport.eqUserId(userId.getValue()))
                .execute();

        em.flush();
        em.clear();
    }
}
