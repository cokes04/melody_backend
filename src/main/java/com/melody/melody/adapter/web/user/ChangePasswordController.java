package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.request.ChangePasswordRequest;
import com.melody.melody.application.service.user.ChangePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@WebAdapter
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordService service;

    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable("userId") long userId, ChangePasswordRequest request){
        ChangePasswordService.Command command = request.to(userId);
        service.execute(command);
        return ResponseEntity.noContent().build();
    }
}
