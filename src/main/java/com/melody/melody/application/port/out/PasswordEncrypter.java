package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Password;

public interface PasswordEncrypter {
    Password encrypt(String rawString);

    boolean matches(String raw, Password encrypted);
}
