package com.melody.melody;

import com.melody.melody.application.port.out.*;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile(value = "dev")
public class AppRunner implements ApplicationRunner {
    private final ImageFileStorage imageFileStorage;
    private final EmotionClassifier emotionClassifier;
    private final MusicGenerator musicGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Resource resource = new ClassPathResource("testImage.jpg");
        GenerateMusicService.Image image = new GenerateMusicService.Image(resource.contentLength(), "image/jpg", resource);
        Music.ImageUrl imageUrl = imageFileStorage.save(image);
        System.out.println(imageUrl);

        Music.Explanation explanation = new Music.Explanation("테스트 테스트");
        System.out.println(explanation);

        Emotion emotion = emotionClassifier.execute(explanation);
        System.out.println(emotion);

        musicGenerator.executeAsync(
                new Music.MusicId(1L),
                emotion,
                20,
                2
        );
    }
}
