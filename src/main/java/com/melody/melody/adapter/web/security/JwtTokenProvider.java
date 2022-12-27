package com.melody.melody.adapter.web.security;

import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Identity;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {
    private final JwtConfig jwtConfig;

    @Override
    public long getIdToAcessToken(String accessToken) {
        return getId(accessToken, jwtConfig.getAccessToken().getSecretKey());
    }

    @Override
    public long getIdToRefreshToken(String refreshToken) {
        return getId(refreshToken, jwtConfig.getRefreshToken().getSecretKey());
    }

    @Override
    public String createAccessToken(Identity userId) {
        return createToken(
                userId,
                jwtConfig.getAccessToken().getValidMilliSecond(),
                jwtConfig.getAccessToken().getSecretKey()
        );
    }

    @Override
    public String createRefreshToken(Identity userId) {
        return createToken(
                userId,
                jwtConfig.getRefreshToken().getValidMilliSecond(),
                jwtConfig.getRefreshToken().getSecretKey()
        );
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtConfig.getAccessToken().getSecretKey());
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtConfig.getRefreshToken().getSecretKey());
    }

    private boolean validateToken(String token, String secretKey) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;

        } catch (SecurityException ex) {
        } catch (MalformedJwtException ex) {
        } catch (ExpiredJwtException ex) {
        } catch (UnsupportedJwtException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (Exception ex){
        }

        return false;
    }

    private String createToken(Identity userId, long validMilliSecond, String secretKey){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validMilliSecond);

        return Jwts.builder()
                .setSubject(String.valueOf(userId.getValue()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    private long getId(String token, String secretKey){
        try {
            String subject = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return Long.parseLong(subject);

        }catch (Exception e){
            throw new FailedAuthenticationException(DomainError.of(UserErrorType.Authentication_Failed));
        }
    }
}
