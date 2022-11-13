package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(User.UserId id);

    boolean existsByEmail(String email);
}
