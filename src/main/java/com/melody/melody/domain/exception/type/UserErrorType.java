package com.melody.melody.domain.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorType implements DomainErrorType{
    Email_Already_Used("0", "이미 사용중인 이메일입니다."),
    Email_Not_Found("0", "존재하지 않는 이메일입니다."),
    Passwod_Not_Matches("0", "패스워드가 일치하지 않습니다."),
    Authentication_Failed("0", "인증에 실패하였습니다.");

    private final String code;
    private final String messageFormat;
}