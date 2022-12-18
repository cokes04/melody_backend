package com.melody.melody.adapter.persistence.music;

import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.TestMusicDomainGenerator;
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
    void toEntity_ShouldReturnEntity() {
        Music music = TestMusicDomainGenerator.randomMusic();

        MusicEntity actual = mapper.toEntity(music);

        assertTrue(music.getId().isPresent());
        assertEquals(music.getId().get().getValue(), actual.getId());
        assertEquals(music.getUserId(), actual.getUserId());
        assertEquals(music.getEmotion(), actual.getEmotion());
        assertEquals(music.getImageUrl().getValue(), actual.getImageUrl());
        assertEquals(music.getExplanation().getValue(), actual.getExplanation());
        assertEquals(music.getStatus(), actual.getStatus());
        assertTrue(music.getMusicUrl().isPresent());
        assertEquals(music.getMusicUrl().get().getValue(), actual.getMusicUrl());
        assertNotNull(actual.getCreatedDate());
    }

    @Test
    void toModel_entity_ShouldReturnModel() {
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();

        Music actual = mapper.toModel(entity);

        assertTrue(actual.getId().isPresent());
        assertEquals(entity.getId(), actual.getId().get().getValue());
        assertEquals(entity.getUserId(), actual.getUserId());
        assertEquals(entity.getEmotion(), actual.getEmotion());
        assertEquals(entity.getImageUrl(), actual.getImageUrl().getValue());
        assertEquals(entity.getExplanation(), actual.getExplanation().getValue());
        assertEquals(entity.getStatus(), actual.getStatus());
        assertTrue(actual.getMusicUrl().isPresent());
        assertEquals(entity.getMusicUrl(), actual.getMusicUrl().get().getValue());
    }

    @Test
    void toModel_data_ShouldReturnModel() {
        MusicData musicData = TestMusicEntityGenerator.randomMusicData();

        Music actual = mapper.toModel(musicData);

        assertTrue(actual.getId().isPresent());
        assertEquals(musicData.getId(), actual.getId().get().getValue());
        assertEquals(musicData.getUserId(), actual.getUserId());
        assertEquals(musicData.getEmotion(), actual.getEmotion());
        assertEquals(musicData.getImageUrl(), actual.getImageUrl().getValue());
        assertEquals(musicData.getExplanation(), actual.getExplanation().getValue());
        assertEquals(musicData.getStatus(), actual.getStatus());
        assertTrue(actual.getMusicUrl().isPresent());
        assertEquals(musicData.getMusicUrl(), actual.getMusicUrl().get().getValue());
    }
}