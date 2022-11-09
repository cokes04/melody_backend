package com.melody.melody.domain.model;

import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class User {
    private UserId id;

    private String lastName;

    private String firstName;

    private String email;

    private Password password;

    public static User create(String lastName, String firstName, String email, Password password){
        return User.builder()
                .id(null)
                .lastName(lastName)
                .firstName(firstName)
                .email(email)
                .password(password)
                .build();
    }

    public Optional<User.UserId> getId(){
        return Optional.ofNullable(this.id);
    }

    @Value
    public static class UserId {
        private final Long value;
    }
}
