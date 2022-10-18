package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestDomainGenerator;
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
    public void getByIdReturnMusic() {
        Music expected = TestDomainGenerator.randomMusic();
        MusicEntity entity = TestEntityGenerator.randomMusicEntity();
        assertTrue(expected.getId().isPresent());
        Music.MusicId musicId = expected.getId().get();

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
    void getByIdReturnEmpty() {
        Music.MusicId musicId = TestDomainGenerator.randomMusicId();

        when(jpaRepository.findById(eq(musicId.getValue())))
                .thenReturn(Optional.empty());

        Optional<Music> actual = musicRepository.getById(musicId);

        assertTrue(actual.isEmpty());

        verify(jpaRepository, times(1)).findById(eq(musicId.getValue()));
        verify(mapper, times(0)).toModel(any(MusicEntity.class));
    }

    @Test
    void saveReturnMusic() {
        Music expected = TestDomainGenerator.randomMusic();
        MusicEntity entity = TestEntityGenerator.randomMusicEntity();

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