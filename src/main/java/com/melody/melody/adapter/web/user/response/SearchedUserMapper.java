package com.melody.melody.adapter.web.user.response;

import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.dto.SearchedUser;
import com.melody.melody.domain.model.Music;

import java.util.stream.Collectors;

public class SearchedUserMapper {
    public static PageResponse<SearchedUserResponse> to(PagingResult<SearchedUser> pagingResult){
        return new PageResponse<SearchedUserResponse>(
                pagingResult.getList().stream().map(SearchedUserMapper::to).collect(Collectors.toList()),
                pagingResult.getCount(),
                pagingResult.getTotalCount(),
                pagingResult.getTotalPage()
        );
    }

    public static SearchedUserResponse to(SearchedUser searchedUser){
        return SearchedUserResponse.builder()
                .userId(searchedUser.getUserId())
                .nickname(searchedUser.getNickName())
                .build();
    }
}