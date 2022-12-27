package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.application.service.post.DeletePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class DeletePostController {
    private final DeletePostService service;

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> delete(@NotNull @Positive @PathVariable("postId") long postId){
        DeletePostService.Command command = new DeletePostService.Command(postId);
        service.execute(command);

        return ResponseEntity.ok().build();
    }
}
