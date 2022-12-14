package com.melody.melody.adapter.web.user.request;

import com.melody.melody.application.service.user.ChangePasswordService;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class ChangePasswordRequest {
    @NotBlank
    @Size(min=10, max=20)
    private final String oldPassword;

    @NotBlank
    @Size(min=10, max=20)
    private final String newPassword;

    public ChangePasswordService.Command toCommand(long userId){
        return new ChangePasswordService.Command(
                userId,
                this.oldPassword,
                this.newPassword
        );
    }
}
