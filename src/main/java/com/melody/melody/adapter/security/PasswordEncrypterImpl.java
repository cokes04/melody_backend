package com.melody.melody.adapter.security;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncrypterImpl implements PasswordEncrypter {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User.Password encrypt(String rawString) {
        return new User.Password(
                passwordEncoder.encode(rawString)
        );
    }

    @Override
    public boolean matches(String raw, User.Password encrypted) {
        return passwordEncoder.matches(
                raw,
                encrypted.getEncryptedString()
        );
    }
}
