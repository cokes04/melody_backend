package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestPostDetailServiceGenerator {
    private static final Faker faker = new Faker();

    public static PostDetail randomPostDetail(){
        Post post = TestPostDomainGenerator.randomOpenPost();
        Music music = TestMusicDomainGenerator.randomMusic();
        User user = TestUserDomainGenerator.randomUser();

        return PostDetail.builder()
                .id(post.getId().get().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .deleted(post.isDeleted())
                .likeCount(post.getLikeCount())
                .open(post.isOpen())
                .createdDate(LocalDateTime.now())

                .musicId(music.getId().get().getValue())
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .musicUrl(music.getMusicUrl().get().getValue())

                .nickname(user.getNickName())
                .userId(user.getId().get().getValue())
                .build();
    }
}
