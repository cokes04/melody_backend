package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.adapter.web.security.Requester;
import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.service.music.GetMusicService;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class GetMusicController {
    private final GetMusicService service;

    @GetMapping(value = "/music/{musicId}")
    public ResponseEntity<MusicResponse> get(@NotNull @Positive @PathVariable("musicId") long mId){
        Music.MusicId musicId = new Music.MusicId(mId);
        GetMusicService.Command command = new GetMusicService.Command(musicId);
        GetMusicService.Result result = service.execute(command);
        MusicResponse musicResponse = MusicResponse.to(result.getMusic());

        return ResponseEntity.ok()
                .body(musicResponse);
    }
}
