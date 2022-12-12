package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PostDetail;
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
public class PostSecurityExpression extends AbstractSecurityExpression{
    private final PostRepository repository;

    public boolean isOwner(Post.PostId postId){
        Optional<Long> postOwnerId = repository.findById(postId).map(m -> m.getUserId().getValue());
        if (postOwnerId.isEmpty()) return true;

        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return postOwnerId.get().equals(optional.get().getUserId());
    }

    public boolean isOwner(PostDetail postDetail){
        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return postDetail.getUserId().equals(optional.get().getUserId());
    }

    public boolean isOpen(PostDetail postDetail){
        return postDetail.isOpen();
    }

    public boolean isOpen(Open open){
        return Open.OnlyOpen.equals(open);
    }

}