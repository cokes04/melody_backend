package com.melody.melody.adapter.web.security;


import com.melody.melody.domain.model.Identity;

import java.util.Optional;

public interface TokenRepository {
    Token save(Token token);

    Optional<Token> findBy(Identity userId);
}
