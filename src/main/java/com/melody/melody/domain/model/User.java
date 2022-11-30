package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.UserErrorType;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class User {
    private UserId id;

    private String nickName;

    private String email;

    private Password password;

    private boolean withdrawn;

    public static User create(String nickName, String email, Password password){
        return User.builder()
                .id(null)
                .nickName(nickName)
                .email(email)
                .password(password)
                .withdrawn(false)
                .build();
    }

    public void withdraw(){
        if (isWithdrawn())
            throw new InvalidStatusException(DomainError.of(UserErrorType.User_Already_Withdawn_Status));

        this.withdrawn = true;
    }

    public Optional<User.UserId> getId(){
        return Optional.ofNullable(this.id);
    }

    @Value
    public static class UserId {
        private final Long value;
    }
}
