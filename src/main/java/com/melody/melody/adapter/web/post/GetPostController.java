package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.post.request.PostPagingRequest;
import com.melody.melody.adapter.web.post.response.PostDetailMapper;
import com.melody.melody.adapter.web.security.Requester;
import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.dto.Open;
import com.melody.melody.application.service.post.GetPostService;
import com.melody.melody.application.service.post.GetUserPostService;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class GetPostController {
    private final GetPostService service;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathParam("postId") Post.PostId postId){

        GetPostService.Command command = new GetPostService.Command(postId);
        GetPostService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(PostDetailMapper.to(result.getPostDetail()));
    }
}
