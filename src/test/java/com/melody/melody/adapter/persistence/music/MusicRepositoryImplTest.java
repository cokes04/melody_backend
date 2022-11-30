package com.melody.melody.adapter.persistence.music;

import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicRepositoryImplTest {

    @InjectMocks
    private MusicRepositoryImpl musicRepository;

    @Mock private MusicJpaRepository jpaRepository;
    @Mock private MusicMapper mapper;

    @Test
    public void getById_ShouldReturnMusic_WhenExistMusic() {
        Music expected = TestMusicDomainGenerator.randomMusic();
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();
        Music.MusicId musicId = expected.getId().get();
        entity.setStatus(Music.Status.COMPLETION);

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.of(entity));

        when(mapper.toModel(eq(entity)))
                .thenReturn(expected);

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(1)).toModel(eq(entity));
    }

    @Test
    void getById_ShouldReturnEmpty_WhenNotExistMusic() {
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.empty());

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isEmpty());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(0)).toModel(any(MusicEntity.class));
    }
    @Test
    void getById_ShouldReturnEmpty_WhenDeletedMusic() {
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();
        Music.MusicId musicId = new Music.MusicId(entity.getId());
        entity.setStatus(Music.Status.DELETED);

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.of(entity));

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isEmpty());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(0)).toModel(any(MusicEntity.class));
    }

    @Test
    void save_ShouldReturnMusic() {
        Music expected = TestMusicDomainGenerator.randomMusic();
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();

        when(mapper.toEntity(eq(expected)))
                .thenReturn(entity);

        when(jpaRepository.save(eq(entity)))
                .thenReturn(entity);

        when(mapper.toModel(eq(entity)))
                .thenReturn(expected);

        Music actual = musicRepository.save(expected);

        assertEquals(expected, actual);

        verify(mapper, times(1)).toEntity(eq(expected));
        verify(jpaRepository, times(1)).save(eq(entity));
        verify(mapper, times(1)).toModel(eq(entity));
    }
}