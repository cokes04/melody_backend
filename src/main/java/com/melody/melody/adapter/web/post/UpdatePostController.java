package com.melody.melody.adapter.web.post;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.post.request.UpdatePostRequest;
import com.melody.melody.application.service.post.UpdatePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@WebAdapter
@Validated
public class UpdatePostController {
    private final UpdatePostService service;

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<?> update(@NotNull @Positive @PathVariable("postId") long postId,
                                    @RequestBody UpdatePostRequest request){
        UpdatePostService.Command command = request.toCommand(postId);
        service.execute(command);

        return ResponseEntity.ok().build();
    }
}
