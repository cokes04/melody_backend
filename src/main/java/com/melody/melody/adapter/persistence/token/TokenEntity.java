package com.melody.melody.adapter.persistence.token;

import com.melody.melody.adapter.web.security.Token;
import com.melody.melody.domain.model.Identity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "TOKEN")
@Table(name = "TOKEN")
public class TokenEntity {

    @Id
    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "REFRESH", unique = true, nullable = false)
    private String refreshToekn;

    @Column(name = "LAST_UPDATED_DATE", nullable = false)
    private LocalDateTime lastUpdatedDate;

    public static TokenEntity toEntity(Token token){
        return TokenEntity.builder()
                .userId(token.getUserId().getValue())
                .refreshToekn(token.getRefreshToken())
                .lastUpdatedDate(token.getLastUpdatedDate())
                .build();
    }

    public Token to(){
        return new Token(Identity.from(userId), refreshToekn, "None", lastUpdatedDate);
    }

    public Token to(String accessToken){
        return new Token(Identity.from(userId), refreshToekn, accessToken, lastUpdatedDate);
    }
}
