package com.melody.melody.adapter.security;

import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordEncrypterImplTest {
    private PasswordEncrypterImpl encrypter;
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = Mockito.mock(PasswordEncoder.class);
        encrypter = new PasswordEncrypterImpl(encoder);
    }

    @Test
    void encrypt_ReturnPassword() {
        String raw = "raw";
        String encrypted = "encrypted";

        when(encoder.encode(eq(raw)))
                .thenReturn(encrypted);

        User.Password actual = encrypter.encrypt(raw);

        assertNotNull(actual);
        assertEquals(encrypted, actual.getEncryptedString());
    }
}