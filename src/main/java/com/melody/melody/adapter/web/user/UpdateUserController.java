package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.request.UpdateUserRequest;
import com.melody.melody.application.service.user.UpdateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class UpdateUserController {
    private final UpdateUserService service;

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> update(@NotNull @Positive @PathVariable("userId") long userId, @RequestBody UpdateUserRequest request){
        UpdateUserService.Command command = new UpdateUserService.Command(userId, request.getNickName());
        service.execute(command);
        return ResponseEntity.ok().build();
    }
}
