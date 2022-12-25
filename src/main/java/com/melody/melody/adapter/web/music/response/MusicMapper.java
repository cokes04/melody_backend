package com.melody.melody.adapter.web.music.response;

import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.domain.model.Music;

import java.util.stream.Collectors;

public class MusicMapper {
    public static PageResponse<MusicResponse> to(PagingResult<Music> pagingResult){
        return new PageResponse<MusicResponse>(
                pagingResult.getList().stream().map(MusicResponse::to).collect(Collectors.toList()),
                pagingResult.getCount(),
                pagingResult.getTotalCount(),
                pagingResult.getTotalPage()
        );
    }
}
