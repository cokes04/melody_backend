package com.melody.melody.adapter.web.user;

import com.melody.melody.adapter.web.WebAdapter;
import com.melody.melody.adapter.web.user.request.UserPagingRequest;
import com.melody.melody.adapter.web.user.response.SearchedUserMapper;
import com.melody.melody.application.service.user.KeywordSearchUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

@WebAdapter
@RequiredArgsConstructor
@Validated
public class KeywordSearchUserController {
    private final KeywordSearchUserService service;

    @GetMapping("/users/search/keyword")
    public ResponseEntity<?> search(@NotBlank @RequestParam("keyword") String keyword, UserPagingRequest userPaging){
        KeywordSearchUserService.Command command = new KeywordSearchUserService.Command(keyword, userPaging.toPagingInfo());
        KeywordSearchUserService.Result result = service.execute(command);

        return ResponseEntity.ok()
                .body(SearchedUserMapper.to(result.getPagingResult()));
    }
}
