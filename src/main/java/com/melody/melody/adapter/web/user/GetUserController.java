package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.request.UserPagingRequest;
import com.melody.melody.adapter.web.user.response.SearchedUserMapper;
import com.melody.melody.adapter.web.user.response.UserResponse;
import com.melody.melody.application.service.user.GetUserService;
import com.melody.melody.application.service.user.KeywordSearchUserService;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class GetUserController {
    private final GetUserService service;

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") User.UserId userId){
        GetUserService.Command command = new GetUserService.Command(userId);
        GetUserService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(UserResponse.to(result.getUser()));
    }
}
