package com.melody.melody.adapter.aws.response;

import com.melody.melody.adapter.aws.AwsErrorType;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.model.Emotion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmotionMapping {
    tense ("분노", "tense", Emotion.TENSE),
    gloomy("슬픔", "gloomy", Emotion.GLOOMY),
    relaxed("중립", "relaxed", Emotion.RELAXED),
    delighted("행복", "delighted", Emotion.DELIGHTED),;

    private final String classification;
    private final String generation;
    private final Emotion emotion;

    public static EmotionMapping of(String classification){
        for (EmotionMapping mapping : EmotionMapping.values()){
            if (mapping.classification.equals(classification))
                return mapping;
        }

        throw new InvalidArgumentException(
                DomainError.of(AwsErrorType.Invalid_Emotion)
        );
    }

    public static EmotionMapping of(Emotion emotion){
        for (EmotionMapping mapping : EmotionMapping.values()){
            if (mapping.emotion.equals(emotion))
                return mapping;
        }

        throw new InvalidArgumentException(
                DomainError.of(AwsErrorType.Invalid_Emotion)
        );
    }
}
