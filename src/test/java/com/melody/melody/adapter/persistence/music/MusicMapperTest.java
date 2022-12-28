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
        assertEqualsModelAndEntity(music, actual);
    }


    @Test
    void toEntity_ShouldReturnNullIdEntity_WhenEmptyIdentity() {
        Music music = TestMusicDomainGenerator.randomEmptyIdentityMusic();

        MusicEntity actual = mapper.toEntity(music);
        assertEqualsModelAndEntity(music, actual);
    }

    @Test
    void toModel_entity_ShouldReturnModel() {
        MusicEntity entity = TestMusicEntityGenerator.randomMusicEntity();

        Music actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);
    }

    @Test
    void toModel_entity_ShouldReturnEmptyIdentityModel_WhenNullId() {
        MusicEntity entity = TestMusicEntityGenerator.randomNullIdMusicEntity();

        Music actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);
    }

    @Test
    void toModel_data_ShouldReturnModel() {
        MusicData musicData = TestMusicEntityGenerator.randomMusicData();

        Music actual = mapper.toModel(musicData);
        assertEqualsModelAndData(actual, musicData);
    }

    @Test
    void toModel_data_ShouldReturnEmptyIdentityModel_WhenNullId() {
        MusicData musicData = TestMusicEntityGenerator.randomNullIdMusicData();

        Music actual = mapper.toModel(musicData);
        assertEqualsModelAndData(actual, musicData);
    }

    private void assertEqualsModelAndEntity(Music music, MusicEntity entity){
        if (music.getId().isEmpty())
            assertNull(entity.getId());
        else
            assertEquals(music.getId().getValue(), entity.getId());

        assertEquals(music.getUserId().getValue(), entity.getUserEntity().getId());
        assertEquals(music.getEmotion(), entity.getEmotion());
        assertEquals(music.getImageUrl().getValue(), entity.getImageUrl());
        assertEquals(music.getExplanation().getValue(), entity.getExplanation());
        assertEquals(music.getStatus(), entity.getStatus());
        assertEquals(music.getMusicUrl().getValue(), entity.getMusicUrl());
        assertNotNull(entity.getCreatedDate());
    }

    private void assertEqualsModelAndData(Music music, MusicData data){
        if (music.getId().isEmpty())
            assertNull(data.getId());
        else
            assertEquals(music.getId().getValue(), data.getId());

        if (music.getMusicUrl().isEmpty())
            assertNull(data.getMusicUrl());
        else
            assertEquals(music.getMusicUrl().getValue(), data.getMusicUrl());

        assertEquals(music.getUserId().getValue(), data.getUserId());
        assertEquals(music.getEmotion(), data.getEmotion());
        assertEquals(music.getImageUrl().getValue(), data.getImageUrl());
        assertEquals(music.getExplanation().getValue(), data.getExplanation());
        assertEquals(music.getStatus(), data.getStatus());
    }
}