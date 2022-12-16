package com.melody.melody.adapter.persistence.user;

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
@Entity(name = "APP_USER")
@Table(name = "APP_USER")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "NICK_NAME", nullable = false, columnDefinition = "varchar(64)")
    private String nickName;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSOWORD", nullable = false)
    private String password;

    @Column(name = "WITHDRAWN", nullable = false)
    private boolean withdrawn;

    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "LAST_ACTIVITY_DATE", nullable = false)
    private LocalDateTime lastActivityDate;
}
