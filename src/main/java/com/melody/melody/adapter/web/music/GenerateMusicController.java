package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.music.request.GenerateMusicRequest;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.adapter.web.security.Requester;
import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.service.music.GenerateMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class GenerateMusicController {
    private final GenerateMusicService service;

    @PostMapping(
            value = "/music",
            consumes = {
                    MediaType.MULTIPART_MIXED_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            }
    )
    public ResponseEntity<MusicResponse> generate(
            @RequestPart(name = "image") MultipartFile image,
            @RequestPart(name = "body") GenerateMusicRequest request){

        GenerateMusicService.Command command = request.toCommand(image);
        GenerateMusicService.Result result = service.execute(command);
        MusicResponse musicResponse = MusicResponse.to(result.getMusic());

        return ResponseEntity.ok()
                .body(musicResponse);
    }
}
