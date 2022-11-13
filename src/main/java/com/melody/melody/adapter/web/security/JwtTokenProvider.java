package com.melody.melody.adapter.web.security;

import com.melody.melody.config.JwtConfig;
import com.melody.melody.domain.model.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {
    private final JwtConfig jwtConfig;

    @Override
    public long getIdToAcessToken(String accessToken) {
        return 0;
    }

    @Override
    public long getIdToRefreshToken(String refreshToken) {
        return 0;
    }

    @Override
    public String createAccessToken(User.UserId id) {
        return createToken(
                id,
                jwtConfig.getAccessToken().getValidMilliSecond(),
                jwtConfig.getAccessToken().getSecretKey()
        );
    }

    @Override
    public String createRefreshToken(User.UserId id) {
        return createToken(
                id,
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

    private String createToken(User.UserId id, long validMilliSecond, String secretKey){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validMilliSecond);

        return Jwts.builder()
                .setSubject(String.valueOf(id.getValue()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
