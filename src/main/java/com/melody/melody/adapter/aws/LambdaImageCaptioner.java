package com.melody.melody.adapter.aws;

import com.melody.melody.application.port.out.ImageCaptioner;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LambdaImageCaptioner implements ImageCaptioner {

    @Override
    public Music.Explanation execute(Music.ImageUrl imageUrl) {
        return null;
    }
}
