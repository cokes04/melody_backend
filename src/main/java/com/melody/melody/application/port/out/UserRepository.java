package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.User;

public interface UserRepository {
    User save(User user);

    boolean existsByEmail(String email);
}
