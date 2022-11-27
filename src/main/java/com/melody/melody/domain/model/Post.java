package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.PostErrorType;
import io.netty.util.internal.StringUtil;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Optional;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class Post {
    private PostId id;

    private Title title;

    private Content content;

    private int likeCount;

    private boolean open;

    private boolean deleted;

    private User.UserId userId;

    private Music.MusicId musicId;

    public static Post create(User.UserId userId, Music.MusicId musicId, String title, String content, boolean open){
        return Post.builder()
                .id(null)
                .title(Title.from(title))
                .content(Content.from(content))
                .open(open)
                .deleted(false)
                .userId(userId)
                .musicId(musicId)
                .build();

    }

    public void update(String title, String content){
        this.title = Title.from(title);
        this.content = Content.from(content);
    }

    public void like(){
        this.likeCount += 1;
    }

    public void notLike(){
        this.likeCount -= 1;
    }

    public void open(){
        this.open = true;
    }

    public void close(){
        this.open = false;
    }

    public void delete(){
        if (isDeleted())
            throw new InvalidStatusException(DomainError.of(PostErrorType.Post_Already_Deleted));

        this.deleted = true;
    }

    public Optional<PostId> getId() {
        return Optional.ofNullable(id);
    }

    @Value
    public static class PostId{
        private final long value;
    }

    @Value
    public static class Title{
        private static final int maxLength = 100;

        private final String value;

        public static Title from(String title){
            if (StringUtil.isNullOrEmpty(title) || title.length() > maxLength)
                throw new InvalidArgumentException(DomainError.of(PostErrorType.Post_Title_Length_Limit_Exceeded));

            return new Title(title);
        }
    }

    @Value
    public static class Content{
        private static final int maxLength = 2000;

        private final String value;

        public static Content from(String content){
            content = content == null ? "" : content;

            if (content.length() > maxLength)
                throw new InvalidArgumentException(DomainError.of(PostErrorType.Post_Content_Length_Limit_Exceeded));

            return new Content(content);
        }
    }
}