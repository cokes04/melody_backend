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
        return randomMusic(randomMusicId(), TestUserDomainGenerator.randomUserId(), Music.Status.COMPLETION, randomMusicUrl());
    }

    public static Music randomCompletionMusic(User.UserId userId){
        return randomMusic(randomMusicId(), userId, Music.Status.COMPLETION, randomMusicUrl());
    }

    public static Music randomProgressMusic(){
        return randomProgressMusic(TestUserDomainGenerator.randomUserId());
    }

    public static Music randomProgressMusic(User.UserId userId){
        return randomMusic(randomMusicId(), userId, Music.Status.PROGRESS, null);
    }

    public static Music randomDeletedMusic(){
        return randomDeletedMusic(TestUserDomainGenerator.randomUserId());
    }

    public static Music randomDeletedMusic(User.UserId userId){
        return randomMusic(randomMusicId(), userId, Music.Status.DELETED, randomMusicUrl());
    }
    public static Music randomMusic(Music.MusicId musicId, User.UserId userId, Music.Status status, Music.MusicUrl musicUrl){
        return Music.builder()
                .id(musicId)
                .userId(userId)
                .emotion(randomEmotion())
                .explanation(randomExplanation())
                .imageUrl(randomImageUrl())
                .status(status)
                .musicUrl(musicUrl)
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
