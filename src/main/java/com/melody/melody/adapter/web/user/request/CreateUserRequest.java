package com.melody.melody.adapter.web.user.request;

import com.melody.melody.application.service.user.CreateUserService;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.*;

@Value
@Builder
public class CreateUserRequest {

    @NotBlank
    @Size(max=50)
    private final String nickName;

    @Email
    private final String email;

    @NotBlank
    @Size(min=10, max=20)
    private final String password;

    public CreateUserService.Command toCommand(){
        return new CreateUserService.Command(
                this.nickName,
                this.email,
                this.password
        );
    }
}
