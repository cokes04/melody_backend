package com.melody.melody.adapter.persistence.user;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    User toModel(UserEntity entity){
        return User.builder()
                .id(entity.getId() == null ? Identity.empty() : Identity.from(entity.getId()))
                .nickName(new User.NickName(entity.getNickName()))
                .email(new User.Email(entity.getEmail()))
                .password(new User.Password(entity.getPassword()))
                .withdrawn(entity.isWithdrawn())
                .build();
    }

    UserEntity toEntity(User user){
        LocalDateTime now = LocalDateTime.now();

        return UserEntity.builder()
                .id(user.getId().isEmpty() ? null : user.getId().getValue())
                .nickName(user.getNickName().getValue())
                .email(user.getEmail().getValue())
                .password(user.getPassword().getEncryptedString())
                .withdrawn(user.isWithdrawn())
                .createdDate(now)
                .lastActivityDate(now)
                .build();
    }
}
