package com.melody.melody.application.dto;

import com.melody.melody.adapter.web.common.PageResponse;
import com.melody.melody.application.dto.PagingResult;
import lombok.*;

import java.util.stream.Collectors;
@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class SearchedUser {
    private long userId;

    private String nickName;
}