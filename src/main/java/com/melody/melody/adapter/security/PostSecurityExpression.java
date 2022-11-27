package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostSecurityExpression {
    private final PostRepository repository;

    @Setter
    private Authentication authentication;

    public boolean isOwner(Post.PostId postId){
        Optional<Long> postOwnerId = repository.findById(postId).map(m -> m.getUserId().getValue());
        if (postOwnerId.isEmpty()) return true;

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return postOwnerId.get().equals(user.getUserId());
    }
}