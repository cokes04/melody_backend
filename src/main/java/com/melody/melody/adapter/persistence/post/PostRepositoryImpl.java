package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostDao postDao;

    @Override
    public Post save(Post post) {
        return postDao.save(post);
    }

    @Override
    public Optional<Post> findById(Identity postId) {
        return postDao.findById(postId);

    }

    public void deleteByUserId(Identity userId){
        postDao.deleteByUserId(userId);
    }
}
