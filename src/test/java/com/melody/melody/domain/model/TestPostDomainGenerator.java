package com.melody.melody.domain.model;

import net.datafaker.Faker;

public class TestPostDomainGenerator {
    private static final Faker faker = new Faker();

    public static Post.PostId randomPostId(){
        return new Post.PostId(faker.number().numberBetween(1, 100000));
    }

    public static Post.Title randomTitle(){
        return Post.Title.from(faker.book().title());
    }

    public static Post.Content randomContent(){
        return Post.Content.from(faker.bossaNova().song());
    }

    public static Post randomOpenPost(){
        return Post.builder()
                .id(randomPostId())
                .userId(TestUserDomainGenerator.randomUserId())
                .musicId(TestMusicDomainGenerator.randomMusicId())
                .deleted(false)
                .open(true)
                .content(randomContent())
                .title(randomTitle())
                .likeCount(0)
                .build();
    }

    public static Post randomClosePost(){
        return Post.builder()
                .id(randomPostId())
                .userId(TestUserDomainGenerator.randomUserId())
                .musicId(TestMusicDomainGenerator.randomMusicId())
                .deleted(false)
                .open(false)
                .content(randomContent())
                .title(randomTitle())
                .likeCount(0)
                .build();
    }

    public static Post randomDeletedPost(){
        return Post.builder()
                .id(randomPostId())
                .userId(TestUserDomainGenerator.randomUserId())
                .musicId(TestMusicDomainGenerator.randomMusicId())
                .deleted(true)
                .open(true)
                .content(randomContent())
                .title(randomTitle())
                .likeCount(0)
                .build();
    }

    public static Post clone(Post post){
        return Post.builder()
                .id(post.getId().orElse(null))
                .userId(post.getUserId())
                .musicId(post.getMusicId())
                .deleted(post.isDeleted())
                .open(post.isOpen())
                .content(post.getContent())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .build();

    }
}
