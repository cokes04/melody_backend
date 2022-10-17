package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.request.MusicRequest;
import com.melody.melody.application.service.music.GenerateMusicService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GenerateMusicCommendMapper{

    public GenerateMusicService.Command of(MusicRequest request, MultipartFile image) {
        return new GenerateMusicService.Command(
                this.to(image),
                request.getMusicLength(),
                request.getNoise()
        );
    }

    private GenerateMusicService.Image to(MultipartFile image){
        return new GenerateMusicService.Image(
                image.getSize(),
                image.getContentType(),
                image.getResource()
        );
    }
}
