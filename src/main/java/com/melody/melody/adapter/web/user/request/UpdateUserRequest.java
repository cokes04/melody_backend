package com.melody.melody.adapter.web.user.request;

import lombok.*;

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
