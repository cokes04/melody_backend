package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class Music {

    private Identity id;

    private Identity userId;

    private Emotion emotion;

    private Explanation explanation;

    private ImageUrl imageUrl;

    private MusicUrl musicUrl;

    private Status status;

    public static Music generate(Identity userId, Emotion emotion, Explanation explanation, ImageUrl imageUrl){
        return new Music(
                Identity.empty(),
                userId,
                emotion,
                explanation,
                imageUrl,
                null,
                Status.PROGRESS
                );
    }

    public void completeGeneration(MusicUrl musicUrl){
        if (!this.status.equals(Status.PROGRESS))
            throw new InvalidStatusException(DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));

        this.musicUrl = musicUrl;
        this.status = Status.COMPLETION;

    }

    public void delete(){
        if (this.status.equals(Status.DELETED))
            throw new InvalidStatusException(DomainError.of(MusicErrorType.Music_Already_Deleted));

        this.status = Status.DELETED;
    }

    public Optional<MusicUrl> getMusicUrl(){
        return Optional.ofNullable(this.musicUrl);
    }

    @Value
    public static class Explanation{
        private final String value;
    }

    @Value
    public static class ImageUrl{
        private final String value;
    }

    @Value
    public static class MusicUrl{
        private final String value;
    }

    public enum Status {
        DELETED, PROGRESS, COMPLETION;
    }

    public enum Emotion {
        DELIGHTED, TENSE, GLOOMY, RELAXED;
    }
}