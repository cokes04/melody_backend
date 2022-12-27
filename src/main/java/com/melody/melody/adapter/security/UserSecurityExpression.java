package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSecurityExpression extends AbstractSecurityExpression {

    public boolean isMe(Identity userId){
        return isMe(userId.getValue());
    }

    public boolean isMe(long userId){
        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        UserDetailsImpl userDetails = optional.get();
        return userDetails.getUserId() == userId;
    }
}