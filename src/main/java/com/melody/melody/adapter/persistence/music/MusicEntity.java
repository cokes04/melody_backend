package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
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
@Entity(name = "MUSIC")
@Table(name = "MUSIC")
public class MusicEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MUSIC_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMOTION", nullable = false, columnDefinition = "varchar(16)")
    private Emotion emotion;

    @Column(name = "EXPLANATION", nullable = false)
    private String explanation;

    @Column(name = "IMAGE_URL", nullable = false, unique = true)
    private String imageUrl;

    @Column(name = "MUSIC_URL", nullable = true, unique = true)
    private String musicUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, columnDefinition = "varchar(16)")
    private Music.Status status;

    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "USER_ID", nullable = true, unique = false)
    private String userId;
}
