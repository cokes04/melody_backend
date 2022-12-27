package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Identity;
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
    void isOwner_musicId_ShouldReturnTrue_WhenMusicOwner() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Identity userId = music.getUserId();
        Identity musicId = music.getId();

        setAuthentication(userId.getValue());

        when(repository.findById(musicId))
                .thenReturn(Optional.of(music));

        boolean actual = expression.isOwner(musicId);
        assertTrue(actual);
    }

    @Test
    void isOwner_musicId_ShouldReturnFalse_WhenOtherUser() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Identity musicId = music.getId();

        long userId = 1234567890L;
        assertNotEquals(music.getUserId().getValue(), userId);
        setAuthentication(userId);

        when(repository.findById(musicId))
                .thenReturn(Optional.of(music));

        boolean actual = expression.isOwner(musicId);
        assertFalse(actual);

    }

    @Test
    void isOwner_musicId_ShouldReturnTrue_WhenNotFoundMusic() {
        Identity musicId = TestMusicDomainGenerator.randomMusicId();

        when(repository.findById(musicId))
                .thenReturn(Optional.empty());

        boolean actual = expression.isOwner(musicId);
        assertTrue(actual);

    }

    @Test
    void isOwner_musicId_ShouldReturnFalse_WhenNullAuthentication() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Identity musicId = music.getId();

        when(repository.findById(musicId))
                .thenReturn(Optional.of(music));

        expression.setAuthentication(null);

        boolean actual = expression.isOwner(musicId);
        assertFalse(actual);
    }


    @Test
    void isOwner_music_ShouldReturnTrue_WhenMusicOwner() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Identity userId = music.getUserId();

        setAuthentication(userId.getValue());

        boolean actual = expression.isOwner(music);
        assertTrue(actual);
    }

    @Test
    void isOwner_music_ShouldReturnFalse_WhenOtherUser() {
        Music music = TestMusicDomainGenerator.randomMusic();

        setAuthentication((music.getUserId().getValue() / 13) + 37);

        boolean actual = expression.isOwner(music);
        assertFalse(actual);

    }

    @Test
    void isOwner_music_ShouldReturnFalse_WhenNullAuthentication() {
        Music music = TestMusicDomainGenerator.randomMusic();
        expression.setAuthentication(null);

        boolean actual = expression.isOwner(music);
        assertFalse(actual);
    }

    @Test
    void isExist_ShouldReturnTrue_WhenExsistMusic() {
        Music music = TestMusicDomainGenerator.randomMusic();
        Identity musicId = music.getId();

        when(repository.findById(musicId))
                .thenReturn(Optional.of(music));

        boolean actual = expression.isExist(musicId);
        assertTrue(actual);

    }

    @Test
    void isExist_ShouldReturnFalse_WhenNotExsistMusic() {
        Identity musicId = TestMusicDomainGenerator.randomMusicId();

        when(repository.findById(musicId))
                .thenReturn(Optional.empty());

        boolean actual = expression.isExist(musicId);
        assertFalse(actual);

    }

    void setAuthentication(long userId){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.builder().userId(userId).build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        expression.setAuthentication(authentication);
    }
}