package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.adapter.web.post.request.PostPagingRequest;
import com.melody.melody.adapter.web.post.response.PostDetailMapper;
import com.melody.melody.adapter.web.post.response.PostDetailResponse;
import com.melody.melody.adapter.web.security.Requester;
import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.service.post.GetUserPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class GetUserPostController {
    private final GetUserPostService service;

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<PageResponse<PostDetailResponse>> getUsersPost(@Requester UserDetailsImpl requester,
                                                                         @NotNull @Positive @PathVariable("userId") long userId,
                                                                         PostPagingRequest paging){

        Open open = getOpen(requester, userId);
        GetUserPostService.Command command = new GetUserPostService.Command(userId, open, paging.toPagingInfo());
        GetUserPostService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(PostDetailMapper.to(result.getPagingResult()));
    }

    private Open getOpen(UserDetailsImpl requester, long userId){
        return requester != null && requester.getUserId() == userId ? Open.Everything : Open.OnlyOpen;
    }
}