package com.melody.melody.adapter.persistence.music;

import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "music")
@Table(name = "music")
public class MusicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Column(nullable = false)
    private String explanation;

    @Column(nullable = false, unique = true)
    private String imageUrl;

    @Column(nullable = true, unique = true)
    private String musicUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Music.Status status;

}
