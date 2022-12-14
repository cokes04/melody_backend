package com.melody.melody.adapter.persistence.comment;

import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "COMMENT")
@Table(name = "COMMENT")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(targetEntity = PostEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ROOT_COMMENT_ID", nullable = true)
    private CommentEntity rootCommentEntity;

    @ManyToOne(targetEntity = PostEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "PERENT_COMMENT_ID", nullable = true)
    private CommentEntity PerentCommentEntity;

    @ManyToOne(targetEntity = PostEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID", nullable = false)
    private PostEntity postEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private LocalDateTime createdDate;
}
