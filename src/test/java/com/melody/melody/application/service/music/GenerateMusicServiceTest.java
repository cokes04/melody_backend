package com.melody.melody.application.service.music;

import com.melody.melody.application.service.TestServiceGenerator;
import com.melody.melody.application.port.out.*;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.MalformedURLException;

import static com.melody.melody.domain.model.TestDomainGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GenerateMusicServiceTest {

    @InjectMocks
    private GenerateMusicService service;

    @Mock private ImageFileStorage imageFileStorage;
    @Mock private ImageCaptioner imageCaptioner;
    @Mock private EmotionClassifier emotionClassifier;
    @Mock private MusicGenerator musicGenerator;
    @Mock private MusicRepository musicRepository;

    @Test
    void execute_ShouldCreateAndReturnMusic() throws MalformedURLException {
        GenerateMusicService.Command command = TestServiceGenerator.randomGenerateMusicCommand();

        Music.MusicId musicId = randomMusicId();
        Music.ImageUrl imageUrl = randomImageUrl();
        Music.Explanation explanation = randomExplanation();
        Emotion emotion = randomEmotion();
        Music expectedMusic = Music.generate(emotion, explanation, imageUrl);
        expectedMusic = insertMusicId(expectedMusic, musicId);

        when(imageFileStorage.save(eq(command.getImage())))
                .thenReturn(imageUrl);

        when(imageCaptioner.execute(eq(imageUrl)))
                .thenReturn(explanation);

        when(emotionClassifier.execute(eq(explanation)))
                .thenReturn(emotion);

        when(musicRepository.save(any(Music.class)))
                .thenAnswer(ans -> insertMusicId(ans.getArgument(0, Music.class), musicId)
                );

        GenerateMusicService.Result result = service.execute(command);

        Music actualMusic;
        assertNotNull((actualMusic = result.getMusic()));
        assertEquals(expectedMusic, actualMusic);

        verify(imageFileStorage, times(1)).save(command.getImage());
        verify(imageCaptioner, times(1)).execute(imageUrl);
        verify(emotionClassifier, times(1)).execute(explanation);
        verify(musicRepository, times(1)).save(any(Music.class));
    }
}