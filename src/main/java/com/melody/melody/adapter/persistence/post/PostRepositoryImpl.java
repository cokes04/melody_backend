package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import static com.melody.melody.adapter.persistence.post.QPostEntity.*;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository jpaRepository;
    private final JPAQueryFactory factory;
    private final PostMapper mapper;

    @Override
    public Post save(Post post) {
        PostEntity entity = mapper.toEntity(post);
        entity = jpaRepository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Optional<Post> findById(Post.PostId postId) {
        Optional<PostEntity> entity = jpaRepository.findById(postId.getValue());
        return entity.filter(e -> !e.isDeleted()).map(mapper::toModel);
    }

    public void deleteByUserId(User.UserId userId){
        factory.update(postEntity)
                .set(postEntity.deleted, true)
                .where(postEntity.userEntity.id.eq(userId.getValue()))
                .execute();

/*
        factory.update(postEntity)
                .where(postEntity.id.in(
                        JPAExpressions.select(postEntity.id)
                                .from(postEntity)
                                .join(postEntity.userEntity, userEntity)
                                .where(postEntity.userEntity.id.eq(userId.getValue()))
                        )
                )
                .execute();
*/

    }
}
