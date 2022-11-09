package com.melody.melody.adapter.security;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.model.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordEncrypterImpl implements PasswordEncrypter {
    private final PasswordEncoder passwordEncoder;

    @Override
    public Password encrypt(String rawString) {
        return new Password(
                passwordEncoder.encode(rawString)
        );
    }

    @Override
    public boolean matches(String raw, Password encrypted) {
        return passwordEncoder.matches(
                raw,
                encrypted.getEncryptedString()
        );
    }
}
