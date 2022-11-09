package com.melody.melody.adapter.web.music.request;

import com.melody.melody.application.service.music.GenerateMusicService;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder
public class GenerateMusicRequest {
    @Positive @NotNull
    private final int musicLength;

    @Positive @NotNull
    private final int noise;

    public GenerateMusicService.Command toCommand(MultipartFile image){
        return new GenerateMusicService.Command(
                this.to(image),
                this.musicLength,
                this.noise
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
