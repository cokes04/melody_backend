package com.melody.melody.adapter.persistence.token;

import com.melody.melody.adapter.persistence.PersistenceAdapter;
import com.melody.melody.adapter.web.security.Token;
import com.melody.melody.adapter.web.security.TokenRepository;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenJpaRepository jpaRepository;

    @Override
    public Token save(Token token) {
        TokenEntity entity = jpaRepository.save(TokenEntity.toEntity(token));
        return entity.to(token.getAccessToken());
    }

    @Override
    public Optional<Token> findBy(Identity userId) {
        return jpaRepository.findById(userId.getValue()).map(TokenEntity::to);
    }
}
