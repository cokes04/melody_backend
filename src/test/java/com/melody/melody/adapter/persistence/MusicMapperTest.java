package com.melody.melody.adapter.persistence;

import com.melody.melody.adapter.persistence.entity.MusicEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestDomainGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicMapperTest {
    private static MusicMapper mapper;

    @BeforeAll
    static void beforeAll() {
        mapper = new MusicMapper();
    }

    @Test
    void toEntityReturnEntity() {
        Music music = TestDomainGenerator.randomMusic();
        assertTrue(music.getId().isPresent());

        MusicEntity actual = mapper.toEntity(music);

        assertEquals(music.getId().get().getValue(), actual.getId());
        assertEquals(music.getEmotion(), actual.getEmotion());
        assertEquals(music.getImageUrl().getValue(), actual.getImageUrl());
        assertEquals(music.getExplanation().getValue(), actual.getExplanation());
        assertEquals(music.getStatus(), actual.getStatus());
    }

    @Test
    void toModelReturnModel() {
        MusicEntity entity = TestEntityGenerator.randomMusicEntity();

        Music actual = mapper.toModel(entity);

        assertTrue(actual.getId().isPresent());
        assertEquals(entity.getId(), actual.getId().get().getValue());
        assertEquals(entity.getEmotion(), actual.getEmotion());
        assertEquals(entity.getImageUrl(), actual.getImageUrl().getValue());
        assertEquals(entity.getExplanation(), actual.getExplanation().getValue());
        assertEquals(entity.getStatus(), actual.getStatus());

    }
}