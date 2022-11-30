package com.melody.melody.application.port.out;

import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;

import java.util.Optional;

public interface PostDetailRepository {
    Optional<PostDetail> findById(Post.PostId postId);
    PagingResult<PostDetail> findByUserId(User.UserId userId, Open open, PagingInfo<PostSort> postPaging);
}
