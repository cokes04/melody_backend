package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.post.request.UpdatePostRequest;
import com.melody.melody.application.service.post.UpdatePostService;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.websocket.server.PathParam;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class UpdatePostController {
    private final UpdatePostService service;

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<?> update(@PathParam("postId") Post.PostId postId,
                                    @RequestBody UpdatePostRequest request){
        UpdatePostService.Command command = request.to(postId);
        service.execute(command);

        return ResponseEntity.ok().build();
    }
}
