package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.application.service.user.WithdrawUserService;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class WithdrawUserController {
    private final WithdrawUserService service;

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> withdraw(@NotNull @Positive @PathVariable long userId){
        service.execute(new WithdrawUserService.Command(new User.UserId(userId)));

        return ResponseEntity.ok().build();
    }

}

