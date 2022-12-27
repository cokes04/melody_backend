package com.melody.melody.application.port.out;

import com.melody.melody.domain.model.Music;

public interface ImageCaptioner {
    Music.Explanation execute(Music.ImageUrl imageUrl);
}