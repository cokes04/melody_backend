package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapper {

    Post toModel(PostEntity entity){
        return Post.builder()
                .id(entity.getId() == null ? Identity.empty() : Identity.from(entity.getId()))
                .title(new Post.Title(entity.getTitle()))
                .content(new Post.Content(entity.getContent()))
                .open(entity.isOpen())
                .deleted(entity.isDeleted())
                .musicId(new Identity(entity.getMusicEntity().getId()))
                .userId(new Identity(entity.getUserEntity().getId()))
                .likeCount(entity.getLikeCount())
                .build();
    }

    PostEntity toEntity(Post post){

        return PostEntity.builder()
                .id(post.getId().isEmpty() ? null : post.getId().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .open(post.isOpen())
                .deleted(post.isDeleted())
                .musicEntity(MusicEntity.builder().id(post.getMusicId().getValue()).build())
                .userEntity(UserEntity.builder().id(post.getUserId().getValue()).build())
                .likeCount(post.getLikeCount())
                .createdDate(LocalDateTime.now())
                .build();
    }
}
