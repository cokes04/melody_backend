package com.melody.melody.application.service.music;

import com.melody.melody.application.port.out.*;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.MusicErrorType;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;
import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateMusicService implements UseCase<GenerateMusicService.Command, GenerateMusicService.Result>{

    private final ImageFileStorage imageFileStorage;
    private final ImageCaptioner imageCaptioner;
    private final EmotionClassifier emotionClassifier;
    private final MusicGenerator musicGenerator;
    private final MusicRepository musicRepository;

    @Override
    public Result execute(Command command){

        Music.ImageUrl imageUrl = imageFileStorage.save(command.getImage());
        Music.Explanation explanation = imageCaptioner.execute(imageUrl);
        Emotion emotion = emotionClassifier.execute(explanation);

        Music music = Music.generate(command.getUserId(), emotion, explanation, imageUrl);
        music = musicRepository.save(music);

        musicGenerator.executeAsync(
                music.getId().orElseThrow( () -> new NotFoundException(DomainError.of(MusicErrorType.Not_Found_Music)) ),
                emotion,
                command.getMusicLength(),
                command.getNoise()
        );

        return new Result(music);
    }

    @Value
    public static class Command implements UseCase.Command{
        private final String userId;
        private final Image image;
        private final int musicLength;
        private final int noise;
    }

    @Value
    public static class Result implements UseCase.Result {
        private final Music music;
    }

    @Value
    public static class Image {
        private final long size;
        private final String mediaType;
        private final Resource resource;
    }
}
