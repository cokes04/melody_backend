package com.melody.melody.adapter.persistence.like;

import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.user.UserEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "POST_LIKE")
@Table(name = "POST_LIKE")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private long id;

    @ManyToOne(targetEntity = PostEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID", nullable = false)
    private PostEntity postEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity userEntity;
}
