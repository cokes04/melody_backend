package com.melody.melody;

import com.melody.melody.adapter.persistence.music.MusicEntity;
import com.melody.melody.adapter.persistence.music.MusicJpaRepository;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.PostJpaRepository;
import com.melody.melody.adapter.persistence.post.size.PostSizeDao;
import com.melody.melody.adapter.persistence.post.size.SizeInfo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Profile("no")
public class Runner implements ApplicationRunner {
    private final UserJpaRepository userJpaRepository;
    private final MusicJpaRepository musicJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PasswordEncrypter encrypter;

    private final PostDetailDao postDetailDao;
    private final PostSizeDao sizeDao;

    // test data
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("runner start");
        Random random = new Random(236854129);
        LocalDateTime now = LocalDateTime.now();
        int count = 1000000;

        /*

        List<MusicEntity> musics = new ArrayList<>(count);
        List<PostEntity> posts = new ArrayList<>(count);
        MusicEntity music;
        PostEntity post;


        UserEntity user = UserEntity.builder()
                .email("testest123@test.com")
                .nickName("닉네임닉네임")
                .password(encrypter.encrypt("abcd12345").getEncryptedString())
                .withdrawn(false)
                .createdDate(now)
                .lastActivityDate(now)
                .build();

        user = userJpaRepository.save(user);

        for (int i = 0; i < count; i ++){
            music = MusicEntity.builder()
                    .musicUrl("musicUrl " + i)
                    .emotion(Music.Emotion.GLOOMY)
                    .explanation("explanation " + i )
                    .imageUrl("imageUrl " + i)
                    .status(Music.Status.COMPLETION)
                    .createdDate(now.minusMinutes(count - i))
                    .userEntity(user)
                    .build();

            musics.add(music);
        }

        musicJpaRepository.saveAll(musics);

        */

        /*
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 500; i++){
            List<MusicEntity> me = musics.subList(i * 2000, (i + 1) * 2000);

            Runnable runnable = () -> musicJpaRepository.saveAll(me);
            Thread thread = new Thread(runnable);
            thread.start();

            threads.add(thread);
        }

        for (Thread t : threads)
            t.join();*/

        /*

        UserEntity entity =UserEntity.builder().id((long) 1).build();
        for (int i = 0; i < count; i ++){
            post = PostEntity.builder()
                    .title("title " + i)
                    .content("content " + i)
                    .open(true)
                    .deleted(false)
                    .likeCount(i)
                    .createdDate(now.minusMinutes(count - i))
                    .userEntity(entity)
                    .musicEntity(MusicEntity.builder().id((long) i + 1).build())
                    .build();

            posts.add(post);
        }

        postJpaRepository.saveAll(posts);


        */

        /*

        threads = new ArrayList<>();
        for (int i = 0; i < 500; i++){
            List<PostEntity> pe = posts.subList(i * 2000, (i + 1) * 2000);

            Runnable runnable = () -> postJpaRepository.saveAll(pe);
            Thread thread = new Thread(runnable);
            thread.start();

            threads.add(thread);
        }

        for (Thread t : threads)
            t.join();*/

        /*


        List<MusicEntity> unplishedMusics = new ArrayList<>(100);
        for (int i = count; i < count+100; i ++){
            music = MusicEntity.builder()
                    .musicUrl("musicUrl " + i)
                    .emotion(Music.Emotion.GLOOMY)
                    .explanation("explanation " + i )
                    .imageUrl("imageUrl " + i)
                    .status(Music.Status.COMPLETION)
                    .createdDate(now.minusMinutes(count+100 - i))
                    .userEntity(entity)
                    .build();

            unplishedMusics.add(music);
        }

        unplishedMusics = musicJpaRepository.saveAll(unplishedMusics);

        */

        /*
        int otherUserCount = 9999;
        int otherMusicPerUserCount = 100;
        int otherPostPerUserCount = 100;

        List<UserEntity> otherUsers = new ArrayList<>(9999);
        List<MusicEntity> otherMusics = new ArrayList<>(100);
        List<PostEntity> otherPosts = new ArrayList<>(100);
        UserEntity otherUser;
        MusicEntity otherMusic;
        PostEntity otherPost;

        String password = encrypter.encrypt("abcd12345").getEncryptedString();
        for (int i = 1; i < otherUserCount + 1; i ++){
            otherUser = UserEntity.builder()
                    .email("testest123" + i + "@test.com")
                    .nickName("닉네임닉네임" + i)
                    .password(password)
                    .withdrawn(false)
                    .createdDate(now)
                    .lastActivityDate(now)
                    .build();

            otherUsers.add(otherUser);
        }
        userJpaRepository.saveAll(otherUsers);


        for (int i = 0; i < otherMusicPerUserCount * otherUserCount; i ++){
            otherMusic = MusicEntity.builder()
                    .musicUrl("musicUrl " + (i + count + 100))
                    .emotion(Music.Emotion.GLOOMY)
                    .explanation("explanation " + (i + count + 100))
                    .imageUrl("imageUrl " + (i + count + 100))
                    .status(Music.Status.COMPLETION)
                    .createdDate(now.minusMinutes(count - (i + count + 100)))
                    .userEntity(UserEntity.builder().id( (long)(i / otherMusicPerUserCount) + 2 ).build())
                    .build();

            otherMusics.add(otherMusic);
        }

        musicJpaRepository.saveAll(otherMusics);

        for (int i = 0; i < otherPostPerUserCount * otherUserCount; i ++){
            otherPost = PostEntity.builder()
                    .title("title " + (i + count))
                    .content("content " + (i + count))
                    .open(randomOpen(random))
                    .deleted(randomDeleted(random))
                    .likeCount(i)
                    .createdDate(now.minusMinutes(count - (i + count)))
                    .userEntity(UserEntity.builder().id( (long)(i / otherPostPerUserCount) + 2 ).build())
                    .musicEntity(MusicEntity.builder().id((long) (i + count + 100 + 1)).build())
                    .build();

            otherPosts.add(otherPost);
        }

        postJpaRepository.saveAll(otherPosts);


         */
    }

    private boolean randomOpen(Random random){
        return random.nextBoolean();
    }

    private boolean randomDeleted(Random random){
        return random.nextInt(100) > 90;
    }

}
