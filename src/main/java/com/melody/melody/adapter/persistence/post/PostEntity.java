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
@Entity(name = "POST")
@Table(name = "POST")
public class PostEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    private int likeCount;

    private boolean open;

    private boolean deleted;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity userEntity;

    @OneToOne(targetEntity = MusicEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "MUSIC_ID", nullable = false)
    private MusicEntity musicEntity;

    @Column(updatable = false)
    private LocalDateTime createdDate;
}
