package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.music.MusicEntity;
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
@EqualsAndHashCode(exclude = {"userEntity", "musicEntity"})
@Entity(name = "POSTA")
@Table(name = "POST")
public class PostEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false, columnDefinition = "varchar(128)")
    private String title;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "varchar(2048)")
    private String content;

    @Column(name = "LIKE_COUNT", nullable = false, columnDefinition = "int")
    private int likeCount;

    @Column(name = "OPEN", nullable = false)
    private boolean open;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity userEntity;

    @OneToOne(targetEntity = MusicEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "MUSIC_ID", nullable = false, unique = true)
    private MusicEntity musicEntity;

    @JoinColumn(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
