package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserSecurityExpression {
    @Setter
    private Authentication authentication;

    public boolean isMe(User.UserId userId){
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return user.getUserId() == user.getUserId();
    }
}