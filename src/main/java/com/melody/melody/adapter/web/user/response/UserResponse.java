package com.melody.melody.adapter.web.user.response;

import com.melody.melody.domain.model.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    private long userId;

    private String nickName;

    private String email;

    public static UserResponse to(User user){
        return UserResponse.builder()
                .userId(user.getId().map(User.UserId::getValue).orElse(-1L))
                .nickName(user.getNickName().getValue())
                .email(user.getEmail().getValue())
                .build();
    }
}
