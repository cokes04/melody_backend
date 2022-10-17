package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.request.MusicRequest;
import com.melody.melody.adapter.web.response.MusicResponse;
import com.melody.melody.application.port.in.UseCase;
import com.melody.melody.application.service.music.GenerateMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class GenerateMusicContoller {
    private final UseCase<GenerateMusicService.Command, GenerateMusicService.Result> service;
    private final GenerateMusicCommendMapper commendMapper;
    private final GenerateMusicResultMapper resultMapper;

    @PostMapping(
            value = "/musics",
            consumes = {
                    MediaType.MULTIPART_MIXED_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            }
    )
    public ResponseEntity<MusicResponse> generate(
            @RequestPart(name = "image") MultipartFile image,
            @RequestPart(name = "body") MusicRequest request ) {

        GenerateMusicService.Command command = commendMapper.of(request, image);
        GenerateMusicService.Result result = service.execute(command);
        MusicResponse musicResponse = resultMapper.to(result);

        return ResponseEntity.ok()
                .body(musicResponse);
    }
}
