package com.melody.melody.adapter.web.user.request;

import com.melody.melody.application.service.user.ChangePasswordService;
import com.melody.melody.domain.model.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UpdateUserRequest {
    @Size(max=30)
    private String nickName;
}
