package com.melody.melody.adapter.persistence.user;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.Password;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    User toModel(UserEntity entity){
        return User.builder()
                .id(new User.UserId(entity.getId()))
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .email(entity.getEmail())
                .password(new Password(entity.getPassword()))
                .withdrawn(entity.isWithdrawn())
                .build();
    }

    UserEntity toEntity(User user){
        return UserEntity.builder()
                .id(user.getId().map(User.UserId::getValue).orElse(null))
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .password(user.getPassword().getEncryptedString())
                .withdrawn(user.isWithdrawn())
                .build();
    }
}
