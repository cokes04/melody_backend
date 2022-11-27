package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Post;

import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Post.PostId postId);
}
