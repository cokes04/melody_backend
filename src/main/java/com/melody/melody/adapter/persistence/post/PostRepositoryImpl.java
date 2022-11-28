package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository jpaRepository;
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
}
