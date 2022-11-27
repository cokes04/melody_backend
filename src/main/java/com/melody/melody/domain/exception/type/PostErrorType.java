package com.melody.melody.domain.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum  PostErrorType implements DomainErrorType{
    Post_Already_Deleted("0", "이미 삭제된 게시물입니다."),
    Post_Title_Length_Limit_Exceeded("0", "제목의 길이 제한을 초과하였습니다."),
    Post_Content_Length_Limit_Exceeded("0", "내용의 길이 제한을 초과하였습니다."),
    Not_Found_Post("0", "요청하신 게시물이 존재하지 않습니다.");


    private final String code;
    private final String messageFormat;
}
