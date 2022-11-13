package com.melody.melody.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    private Token accessToken;

    private Token refreshToken;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Token{
        private long validMilliSecond;
        private String secretKey;
        private String name;
    }
}