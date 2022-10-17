package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;

public interface MusicGenerator {
    void executeAsync(Music.MusicId musicId, Emotion emotion, int musicLength, int noise);
}