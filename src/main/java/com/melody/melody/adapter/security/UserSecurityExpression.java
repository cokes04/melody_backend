package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class UserSecurityExpression extends AbstractSecurityExpression {

    public boolean isMe(User.UserId userId){
        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        UserDetailsImpl userDetails = optional.get();
        return userDetails.getUserId() == userId.getValue();
    }
}