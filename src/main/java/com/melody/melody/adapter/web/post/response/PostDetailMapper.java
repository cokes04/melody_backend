package com.melody.melody.adapter.web.post.response;

import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.adapter.web.music.response.MusicResponse;
import com.melody.melody.adapter.web.user.response.WriterResponse;
import com.melody.melody.application.dto.PagingResult;
import com.melody.melody.application.dto.PostDetail;

import java.util.stream.Collectors;

public class PostDetailMapper {
    public static PageResponse<PostDetailResponse> to(PagingResult<PostDetail> pagingResult){
        return new PageResponse<PostDetailResponse>(
                pagingResult.getList().stream().map(PostDetailMapper::to).collect(Collectors.toList()),
                pagingResult.getCount(),
                pagingResult.getTotalCount(),
                pagingResult.getTotalPage()
        );
    }

    public static PostDetailResponse to(PostDetail postDetail){
        return PostDetailResponse.builder()
                .postId(postDetail.getId())
                .title(postDetail.getTitle())
                .content(postDetail.getContent())
                .likeCount(postDetail.getLikeCount())
                .open(postDetail.isOpen())
                .deleted(postDetail.isDeleted())
                .createdDate(postDetail.getCreatedDate())
                .music(toMusicResponse(postDetail))
                .writer(toWriterResponse(postDetail))
                .build();
    }

    private static MusicResponse toMusicResponse(PostDetail postDetail){
        return MusicResponse.builder()
                .musicId(postDetail.getMusicId())
                .userId(postDetail.getUserId())
                .musicUrl(postDetail.getMusicUrl())
                .emotion(postDetail.getEmotion())
                .explanation(postDetail.getExplanation())
                .imageUrl(postDetail.getImageUrl())
                .status(postDetail.getMusicStatus())
                .build();
    }

    private static WriterResponse toWriterResponse(PostDetail postDetail){
        return WriterResponse.builder()
                .userId(postDetail.getUserId())
                .nickname(postDetail.getNickname())
                .build();
    }

}
