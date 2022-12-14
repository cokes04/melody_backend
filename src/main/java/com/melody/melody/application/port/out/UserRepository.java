package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.User;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(User.Email email);

    Optional<User> findById(User.UserId id);

    boolean existsByEmail(User.Email email);
}
