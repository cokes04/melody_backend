package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.response.UserResponse;
import com.melody.melody.application.service.user.GetUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class GetUserController {
    private final GetUserService service;

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@NotNull @Positive @PathVariable("userId") long userId){
        GetUserService.Command command = new GetUserService.Command(userId);
        GetUserService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(UserResponse.to(result.getUser()));
    }
}
