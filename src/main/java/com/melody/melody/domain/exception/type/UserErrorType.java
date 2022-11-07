package com.melody.melody.domain.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorType implements DomainErrorType{
    Email_Already_Used("0", "이미 사용중인 이메일 입니다.");

    private final String code;
    private final String messageFormat;
}