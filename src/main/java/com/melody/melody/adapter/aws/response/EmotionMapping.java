package com.melody.melody.adapter.aws.response;

import com.melody.melody.adapter.aws.AwsErrorType;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.model.Music;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmotionMapping {
    tense ("분노", "tense", Music.Emotion.TENSE),
    gloomy("슬픔", "gloomy", Music.Emotion.GLOOMY),
    relaxed("중립", "relaxed", Music.Emotion.RELAXED),
    delighted("행복", "delighted", Music.Emotion.DELIGHTED),;

    private final String classification;
    private final String generation;
    private final Music.Emotion emotion;

    public static EmotionMapping of(String classification){
        for (EmotionMapping mapping : EmotionMapping.values()){
            if (mapping.classification.equals(classification))
                return mapping;
        }

        throw new InvalidArgumentException(
                DomainError.of(AwsErrorType.Invalid_Emotion)
        );
    }

    public static EmotionMapping of(Music.Emotion emotion){
        for (EmotionMapping mapping : EmotionMapping.values()){
            if (mapping.emotion.equals(emotion))
                return mapping;
        }

        throw new InvalidArgumentException(
                DomainError.of(AwsErrorType.Invalid_Emotion)
        );
    }
}
