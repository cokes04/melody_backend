package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.application.service.post.DeletePostService;
import com.melody.melody.domain.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.websocket.server.PathParam;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class DeletePostController {
    private final DeletePostService service;

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> delete(@PathParam("postId") Post.PostId postId){
        DeletePostService.Command command = new DeletePostService.Command(postId);
        service.execute(command);

        return ResponseEntity.ok().build();
    }
}
