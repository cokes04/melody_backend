package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.User;

public interface PasswordEncrypter {
    User.Password encrypt(String rawString);

    boolean matches(String raw, User.Password encrypted);
}
