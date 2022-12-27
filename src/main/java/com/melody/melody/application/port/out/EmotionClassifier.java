package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Music;

public interface EmotionClassifier {
    Music.Emotion execute(Music.Explanation explanation);
}
