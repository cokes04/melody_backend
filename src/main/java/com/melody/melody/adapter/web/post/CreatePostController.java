package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.post.request.CreatePostRequest;
import com.melody.melody.adapter.web.security.Requester;
import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.service.post.CreatePostService;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class CreatePostController {
    private final CreatePostService service;

    @PostMapping("/posts")
    public ResponseEntity<?> create(@Requester UserDetailsImpl requester,
                                    @RequestBody CreatePostRequest request){
        CreatePostService.Command command = request.toCommand(requester.getUserId());
        CreatePostService.Result result = service.execute(command);
        long postId = result.getPost().getId().map(Post.PostId::getValue).get();

        return ResponseEntity
                .created(URI.create("/posts/" + postId))
                .build();
    }
}
