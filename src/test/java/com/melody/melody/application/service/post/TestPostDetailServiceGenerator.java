package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.domain.model.*;
import net.datafaker.Faker;

import java.time.LocalDateTime;

public class TestPostDetailServiceGenerator {
    private static final Faker faker = new Faker();

    public static PostDetail randomPostDetail(){
        long userId = TestUserDomainGenerator.randomUserId().getValue();
        return randomPostDetail(userId, true, false);
    }

    public static PostDetail randomPostDetail(long userId , boolean open, boolean deleted){
        Post post = TestPostDomainGenerator.randomOpenPost();
        Music music = TestMusicDomainGenerator.randomMusic();

        return PostDetail.builder()
                .id(post.getId().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .deleted(deleted)
                .likeCount(post.getLikeCount())
                .open(open)
                .createdDate(LocalDateTime.now())

                .musicId(music.getId().getValue())
                .emotion(music.getEmotion())
                .explanation(music.getExplanation().getValue())
                .imageUrl(music.getImageUrl().getValue())
                .musicUrl(music.getMusicUrl().get().getValue())
                .musicStatus(music.getStatus())

                .nickname(TestUserDomainGenerator.randomNickName().getValue())
                .userId(userId)
                .build();
    }


}
