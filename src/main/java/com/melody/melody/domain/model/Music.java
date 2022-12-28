package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.MusicErrorType;
import io.netty.util.internal.StringUtil;
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
                MusicUrl.empty(),
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


    @Value
    public static class Explanation{
        private static final int maxLength = 1000;

        private final String value;

        public static Explanation from(String explanation){
            if (StringUtil.isNullOrEmpty(explanation))
                throw new InvalidArgumentException(DomainError.of(MusicErrorType.Not_Exist_Music_Explanation));

            if (explanation.length() > maxLength)
                explanation = explanation.substring(maxLength);

            return new Explanation(explanation);
        }
    }

    @Value
    public static class ImageUrl{
        private final String value;

        public static ImageUrl from(String imageUrl){
            if (StringUtil.isNullOrEmpty(imageUrl))
                throw new InvalidArgumentException(DomainError.of(MusicErrorType.Not_Exist_ImageUrl));

            return new ImageUrl(imageUrl);
        }
    }

    @Value
    public static class MusicUrl{
        private final String value;

        public static MusicUrl from(String musicUrl){
            if (StringUtil.isNullOrEmpty(musicUrl))
                throw new InvalidArgumentException(DomainError.of(MusicErrorType.Not_Exist_MusicUrl));

            return new MusicUrl(musicUrl);
        }

        public static MusicUrl empty(){
            return new MusicUrl("");
        }

        public boolean isEmpty(){
            return "".equals(value);
        }

    }

    public enum Status {
        DELETED, PROGRESS, COMPLETION;
    }

    public enum Emotion {
        DELIGHTED, TENSE, GLOOMY, RELAXED;
    }
}