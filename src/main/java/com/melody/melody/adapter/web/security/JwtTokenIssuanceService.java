package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Identity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuanceService implements TokenIssuanceService {

    @Qualifier("JwtTokenManager")
    private final TokenIssuancer issuancer;

    private final TokenRepository repository;

    // 사용자당 토큰 1개
    @Transactional
    @Override
    public Token issuance(Identity userId) {
        String refresh = issuancer.issuanceRefreshToken(userId);
        String access = issuancer.issuanceAccessToken(userId);

        return repository.save(Token.create(userId, refresh, access));
    }

    @Transactional
    @Override
    public Token validateAndIssuance(Identity userId, String refreshToken)  {
        Token existing = repository.findBy(userId)
                .orElseThrow(() -> new FailedAuthenticationException(DomainError.of(UserErrorType.Invalid_Authentication)));

        if (existing.validate(refreshToken)){
            String refresh = issuancer.issuanceRefreshToken(userId);
            String access = issuancer.issuanceAccessToken(userId);

            return repository.save(Token.create(userId, refresh, access));

        }else {
            throw new FailedAuthenticationException(DomainError.of(UserErrorType.Invalid_Authentication));

        }
    }
}
