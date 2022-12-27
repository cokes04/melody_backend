package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;

public interface MusicGenerator {
    void executeAsync(Identity musicId, Music.Emotion emotion, int musicLength, int noise);
}