package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapper {

    Post toModel(PostEntity entity){
        return Post.builder()
                .id(new Post.PostId(entity.getId()))
                .title(new Post.Title(entity.getTitle()))
                .content(new Post.Content(entity.getContent()))
                .open(entity.isOpen())
                .deleted(entity.isDeleted())
                .musicId(new Music.MusicId(entity.getMusicEntity().getId()))
                .userId(new User.UserId(entity.getUserEntity().getId()))
                .build();
    }

    PostEntity toEntity(Post post){
        Long id = post.getId().map(Post.PostId::getValue).orElse(null);

        return PostEntity.builder()
                .id(id)
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .open(post.isOpen())
                .deleted(post.isDeleted())
                .musicEntity(MusicEntity.builder().id(post.getMusicId().getValue()).build())
                .UserEntity(UserEntity.builder().id(post.getUserId().getValue()).build())
                .build();
    }
}
