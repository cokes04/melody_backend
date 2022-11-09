package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.user.request.CreateUserRequest;
import com.melody.melody.application.service.user.CreateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateUserContoller {
    private final CreateUserService service;

    @PostMapping("/users")
    public ResponseEntity<?> create(CreateUserRequest request){
        CreateUserService.Command command = request.toCommand();
        service.execute(command);

        return ResponseEntity.noContent().build();
    }
}
