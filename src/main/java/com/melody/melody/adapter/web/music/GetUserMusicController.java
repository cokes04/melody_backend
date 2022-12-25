package com.melody.melody.adapter.web.music;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.adapter.web.music.request.MusicPagingRequest;
import com.melody.melody.adapter.web.music.response.MusicMapper;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.application.dto.MusicSort;
import com.melody.melody.application.dto.PagingInfo;
import com.melody.melody.application.service.music.GetUserMusicService;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class GetUserMusicController {
    private final GetUserMusicService service;

    @GetMapping(value = "/users/{userId}/music")
    public ResponseEntity<PageResponse<MusicResponse>> getUsersMusic(@NotNull @PathVariable("userId") User.UserId userId,
                                                                     @NotNull @RequestParam("publish") MusicPublish publish,
                                                                     MusicPagingRequest paging){
        PagingInfo<MusicSort> musicPaging = paging.toPagingInfo();
        GetUserMusicService.Command command = new GetUserMusicService.Command(userId, publish, musicPaging);
        GetUserMusicService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(MusicMapper.to(result.getPagingResult()));
    }
}
