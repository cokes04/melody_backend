package com.melody.melody.domain.model;

import net.datafaker.Faker;

public class TestMusicDomainGenerator {
    private static final Faker faker = new Faker();

    public static Music.MusicId randomMusicId(){
        return new Music.MusicId(
                faker.number().numberBetween(1L, Long.MAX_VALUE)
        );
    }

    public static Emotion randomEmotion(){
        return faker.options().option(Emotion.class);
    }

    public static Music.Explanation randomExplanation(){
        return new Music.Explanation(
                faker.book().title()
        );
    }

    public static Music.ImageUrl randomImageUrl(){
        return new Music.ImageUrl(
                faker.internet().url()
        );
    }

    public static Music.MusicUrl randomMusicUrl(){
        return new Music.MusicUrl(
                faker.internet().url()
        );
    }

    public static Music.Status randomStatus(){
        return faker.options().option(Music.Status.class);
    }

    public static Music generatedMusic(){
        return Music.generate(
                TestUserDomainGenerator.randomUserId(),
                randomEmotion(),
                randomExplanation(),
                randomImageUrl()
        );
    }

    public static Music randomMusic(){
        return Music.builder()
                .userId(TestUserDomainGenerator.randomUserId())
                .id(randomMusicId())
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .status(randomStatus())
                .musicUrl(randomMusicUrl())
                .build();
    }

    public static Music cloneMusic(Music music){
        return Music.builder()
                .userId(music.getUserId())
                .id(music.getId().orElse(null))
                .emotion(music.getEmotion())
                .explanation(music.getExplanation())
                .imageUrl(music.getImageUrl())
                .status(music.getStatus())
                .musicUrl(music.getMusicUrl().orElse(null))
                .build();
    }

    public static Music insertMusicId(Music music, Music.MusicId musicId){
        return Music.builder()
                .id(musicId)
                .userId(music.getUserId())
                .emotion(music.getEmotion())
                .explanation(music.getExplanation())
                .imageUrl(music.getImageUrl())
                .status(music.getStatus())
                .musicUrl(music.getMusicUrl().orElse(null))
                .build();
    }
}
