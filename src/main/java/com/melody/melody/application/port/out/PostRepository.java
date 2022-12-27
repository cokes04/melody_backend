package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;

import java.util.Optional;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(Identity postId);
    void deleteByUserId(Identity userId);
}
