package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostSecurityExpression extends AbstractSecurityExpression{
    private final PostRepository repository;

    public boolean isOwner(Identity postId){
        Optional<Long> postOwnerId = repository.findById(postId).map(m -> m.getUserId().getValue());
        if (postOwnerId.isEmpty()) return true;

        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return postOwnerId.get().equals(optional.get().getUserId());
    }

    public boolean isOwner(long postId){
        return isOwner(Identity.from(postId));
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