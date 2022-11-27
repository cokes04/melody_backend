package com.melody.melody.domain.model;

import net.datafaker.Faker;

public class TestPostDomainGenerator {
    private static final Faker faker = new Faker();

    public static Post.Title randomTitle(){
        return Post.Title.from(faker.book().title());
    }

    public static Post.Content randomContent(){
        return Post.Content.from(faker.bossaNova().song());
    }

    public static Post randomOpenPost(){
        return Post.create(TestUserDomainGenerator.randomUserId(), TestMusicDomainGenerator.randomMusicId(), randomTitle().getValue(), randomContent().getValue(), true);
    }

    public static Post randomClosePost(){
        Post post = randomOpenPost();
        post.close();
        return post;
    }

    public static Post randomDeletedPost(){
        Post post = randomOpenPost();
        post.delete();
        return post;
    }
}
