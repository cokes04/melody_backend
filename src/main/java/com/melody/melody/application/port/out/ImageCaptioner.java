package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import org.springframework.core.io.Resource;

public interface ImageCaptioner {
    Music.Explanation execute(Music.ImageUrl imageUrl);
}