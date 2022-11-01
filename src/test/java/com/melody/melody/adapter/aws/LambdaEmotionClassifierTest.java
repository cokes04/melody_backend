package com.melody.melody.adapter.aws;

import com.melody.melody.config.JasyptConfig;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles(profiles = "dev")
@EnableEncryptableProperties
@ContextConfiguration(classes = {JasyptConfig.class})
@SpringBootTest(classes = {LambdaEmotionClassifier.class})
class LambdaEmotionClassifierTest {

    @Autowired
    private LambdaEmotionClassifier classifier;

    @Test
    void execute_returnEmotion() {
        Music.Explanation caption = new Music.Explanation("안녕하다 행복하다");

        Emotion emotion = classifier.execute(caption);

        assertNotNull(emotion);

    }
}