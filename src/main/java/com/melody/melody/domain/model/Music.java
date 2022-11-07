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

    private MusicId id;

    private Emotion emotion;

    private Explanation explanation;

    private ImageUrl imageUrl;

    private Status status;

    public static Music generate(Emotion emotion, Explanation explanation, ImageUrl imageUrl){
        return new Music(
                null,
                emotion,
                explanation,
                imageUrl,
                Status.PROGRESS
                );
    }

    public void completeGeneration(){
        if (!this.status.equals(Status.PROGRESS))
            throw new InvalidStatusException(DomainError.of(MusicErrorType.Should_Be_Progress_State_For_Complete_Generation));

        this.status = Status.COMPLETION;

    }

    public Optional<MusicId> getId(){
        return Optional.ofNullable(this.id);
    }

    @Value
    public static class MusicId {
        private final Long value;
    }

    @Value
    public static class Explanation{
        private final String value;
    }

    @Value
    public static class ImageUrl{
        private final String value;

    }

    public enum Status {
        PROGRESS, COMPLETION;
    }
}