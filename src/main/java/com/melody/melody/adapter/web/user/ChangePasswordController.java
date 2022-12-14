package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.request.ChangePasswordRequest;
import com.melody.melody.application.service.user.ChangePasswordService;
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
public class ChangePasswordController {
    private final ChangePasswordService service;

    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<?> changePassword(@NotNull @Positive @PathVariable("userId") long userId, @RequestBody ChangePasswordRequest request){
        ChangePasswordService.Command command = request.toCommand(userId);
        service.execute(command);
        return ResponseEntity.ok().build();
    }
}
