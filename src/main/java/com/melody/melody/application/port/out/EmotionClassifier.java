package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;

public interface EmotionClassifier {
    Emotion execute(Music.Explanation explanation);
}
