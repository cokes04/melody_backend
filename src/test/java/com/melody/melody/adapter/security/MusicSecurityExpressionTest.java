package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MusicSecurityExpressionTest {

    private MusicSecurityExpression expression;

    private MusicRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MusicRepository.class);
        expression = new MusicSecurityExpression(repository);
    }

    @Test
    void isOwner_ShouldReturnTrue_WhenMusicOwner() {
        Music music = TestMusicDomainGenerator.randomMusic();
        User.UserId userId = music.getUserId();
        Music.MusicId musicId = music.getId().get();

        setAuthentication(userId.getValue());

        when(repository.getById(musicId))
                .thenReturn(Optional.of(music));

        boolean actual = expression.isOwner(musicId);
        assertTrue(actual);
    }

    @Test
    void isOwner_ShouldReturnFalse_WhenOtherUser() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Music.MusicId musicId = music.getId().get();

        long userId = 1234567890L;
        assertNotEquals(music.getUserId().getValue(), userId);
        setAuthentication(userId);

        when(repository.getById(musicId))
                .thenReturn(Optional.of(music));

        boolean actual = expression.isOwner(musicId);
        assertFalse(actual);

    }

    @Test
    void isOwner_ShouldReturnTrue_WhenNotFoundMusic() {
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();

        when(repository.getById(musicId))
                .thenReturn(Optional.empty());

        boolean actual = expression.isOwner(musicId);
        assertTrue(actual);

    }

    void setAuthentication(long userId){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.builder().userId(userId).build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        expression.setAuthentication(authentication);
    }
}