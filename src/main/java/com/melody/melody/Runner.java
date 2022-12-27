package com.melody.melody;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.MusicJpaRepository;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.PostJpaRepository;
import com.melody.melody.adapter.persistence.postdetail.PostDetailDao;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.adapter.persistence.user.UserJpaRepository;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Profile("dev123")
public class Runner implements ApplicationRunner {
    private final UserJpaRepository userJpaRepository;
    private final MusicJpaRepository musicJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PasswordEncrypter encrypter;

    private final PostDetailDao postDetailDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDateTime now = LocalDateTime.now();

        UserEntity user = UserEntity.builder()
                .email("testest123@test.com")
                .nickName("닉네임닉네임")
                .password(encrypter.encrypt("abcd12345").getEncryptedString())
                .withdrawn(false)
                .createdDate(now)
                .lastActivityDate(now)
                .build();

        user = userJpaRepository.save(user);

        MusicEntity music;
        PostEntity post;

        Random random = new Random(236854129);
        for (int i = 0; i < 5000; i ++){
            music = MusicEntity.builder()
                    .musicUrl("musicUrl " + i)
                    .emotion(Music.Emotion.GLOOMY)
                    .explanation("explanation " + i )
                    .imageUrl("imageUrl " + i)
                    .status(Music.Status.COMPLETION)
                    .createdDate(now.minusMinutes(5000 - i))
                    .userEntity(user)
                    .build();

            music = musicJpaRepository.save(music);

            post = PostEntity.builder()
                    .title("title " + i)
                    .content("content " + i)
                    .open(randomOpen(random))
                    .deleted(randomDeleted(random))
                    .likeCount(i)
                    .createdDate(now.minusMinutes(5000 - i))
                    .userEntity(user)
                    .musicEntity(music)
                    .build();

            postJpaRepository.save(post);
        }

        for (int i = 5000; i < 5200; i ++){
            music = MusicEntity.builder()
                    .musicUrl("musicUrl " + i)
                    .emotion(Music.Emotion.GLOOMY)
                    .explanation("explanation " + i )
                    .imageUrl("imageUrl " + i)
                    .status(Music.Status.COMPLETION)
                    .createdDate(now.minusMinutes(5200 - i))
                    .userEntity(user)
                    .build();

            music = musicJpaRepository.save(music);
        }

        postDetailDao.findByUserId(
                Identity.from(user.getId()),
                Open.Everything,
                new PagingInfo<PostSort>(0, 20, PostSort.newest)
        );

        postDetailDao.findTotalSizeByUserId(Identity.from(user.getId()), Open.OnlyClose);
        postDetailDao.findTotalSizeByUserId(Identity.from(user.getId()), Open.OnlyOpen);
        postDetailDao.findTotalSizeByUserId(Identity.from(user.getId()), Open.Everything);
    }

    private boolean randomOpen(Random random){
        return random.nextBoolean();
    }

    private boolean randomDeleted(Random random){
        return random.nextInt(100) > 80;
    }
}
