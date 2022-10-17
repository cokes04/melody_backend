package com.melody.melody.application.port.out;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.Music;

public interface ImageFileStorage {
    Music.ImageUrl save(GenerateMusicService.Image image);

}
