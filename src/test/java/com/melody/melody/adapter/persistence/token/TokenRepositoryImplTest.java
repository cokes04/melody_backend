package com.melody.melody.adapter.persistence.token;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.TestMusicEntityGenerator;
import com.melody.melody.adapter.persistence.searchedUser.SearchedUserRepositoryImpl;
import com.melody.melody.adapter.web.security.TestTokenWebGenerator;
import com.melody.melody.adapter.web.security.Token;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
@ActiveProfiles("dbtest")
class TokenRepositoryImplTest {
    private TokenRepositoryImpl repository;

    @Autowired
    private TokenJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        repository = new TokenRepositoryImpl(jpaRepository);
    }

    @Test
    void save_ShouldReturnToken() {
        Token token = TestTokenWebGenerator.randomToken();

        Token actual = repository.save(token);

        TokenEntity entity = em.find(TokenEntity.class, token.getUserId().getValue());

        assertEqualsTokenAndEntity(actual, entity);
        assertEquals(token, actual);
    }

    @Test
    void findBy_ShouldReturnToken_WhenExistedUserId() {
        Identity userId = TestTokenWebGenerator.randomUserId();
        TokenEntity entity = new TokenEntity(userId.getValue(), TestTokenWebGenerator.randomRefreshToken(), TestTokenWebGenerator.nowLastUpdatedDate());
        em.persist(entity);
        em.flush();
        em.clear();

        Optional<Token> actual = repository.findBy(userId);

        assertTrue(actual.isPresent());
        assertEqualsTokenAndEntity(actual.get(), entity);
    }


    @Test
    void findBy_ShouldReturnEmpty_WhenNotExistedUserId() {
        Identity userId = TestTokenWebGenerator.randomUserId();

        Optional<Token> actual = repository.findBy(userId);

        assertTrue(actual.isEmpty());
    }

    private void assertEqualsTokenAndEntity(Token token, TokenEntity entity){
        assertEquals(token.getUserId().getValue(), entity.getUserId());
        assertEquals(token.getRefreshToken(), entity.getRefreshToekn());
        assertEquals(token.getLastUpdatedDate(), entity.getLastUpdatedDate());
    }

}