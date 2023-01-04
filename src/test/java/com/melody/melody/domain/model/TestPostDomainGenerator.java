package com.melody.melody.domain.model;

import com.melody.melody.application.dto.Open;
import net.datafaker.Faker;

public class TestPostDomainGenerator {
    private static final Faker faker = new Faker();


    public static Post randomOpenPost(){
        return getPostBuilder().open(true).deleted(false).build();

    }

    public static Post randomOpenPost(Identity userId){
        return getPostBuilder().userId(userId).open(true).deleted(false).build();

    }

    public static Post randomOpenPost(Identity postId, Identity userId){
        return getPostBuilder().id(postId).userId(userId).open(true).deleted(false).build();

    }

    public static Post randomClosePost(){
        return getPostBuilder().open(false).deleted(false).build();

    }

    public static Post randomClosePost(Identity userId){
        return getPostBuilder().userId(userId).open(false).deleted(false).build();

    }

    public static Post randomDeletedPost(){
        return getPostBuilder().open(true).deleted(true).build();
    }

    public static Post randomEmptyIdentityPost(){
        return getPostBuilder().id(Identity.empty()).open(true).deleted(false).build();
    }

    public static Post clonePost(Post post){
        return Post.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .musicId(post.getMusicId())
                .deleted(post.isDeleted())
                .open(post.isOpen())
                .content(post.getContent())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .build();

    }

    public static Post insertIdPost(Post post, Identity postId){
        return Post.builder()
                .id(postId)
                .userId(post.getUserId())
                .musicId(post.getMusicId())
                .deleted(post.isDeleted())
                .open(post.isOpen())
                .content(post.getContent())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .build();
    }

    public static Identity randomPostId(){
        return Identity.from(faker.number().numberBetween(1L, Long.MAX_VALUE));
    }

    public static Post.Title randomTitle(){
        return Post.Title.from(faker.book().title());
    }

    public static Post.Content randomContent(){
        return Post.Content.from(faker.bossaNova().song());
    }

    public static int randomLikeCount(){
        return faker.number().numberBetween(0, 100000);
    }

    private static Post.PostBuilder getPostBuilder(){
        return Post.builder()
                .id(randomPostId())
                .userId(TestUserDomainGenerator.randomUserId())
                .musicId(TestMusicDomainGenerator.randomMusicId())
                .content(randomContent())
                .title(randomTitle())
                .likeCount(randomLikeCount());
    }
}
