package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.post.response.PostDetailMapper;
import com.melody.melody.application.service.post.GetPostService;
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
public class GetPostController {
    private final GetPostService service;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@NotNull @Positive @PathVariable("postId") long postId){

        GetPostService.Command command = new GetPostService.Command(postId);
        GetPostService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(PostDetailMapper.to(result.getPostDetail()));
    }
}
