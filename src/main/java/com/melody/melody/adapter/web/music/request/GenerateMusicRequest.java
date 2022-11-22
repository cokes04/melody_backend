package com.melody.melody.adapter.web.music.request;

import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.model.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateMusicRequest {
    @Positive @NotNull
    private int musicLength;

    @Positive @NotNull
    private int noise;

    public GenerateMusicService.Command toCommand(MultipartFile image, long userId){
        return new GenerateMusicService.Command(
                new User.UserId(userId),
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
