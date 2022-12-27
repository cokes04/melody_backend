package com.melody.melody.domain.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorType implements DomainErrorType {
    Invalid_Identity("000000","올바르지 않은 식별자입니다."),;

    private final String code;
    private final String messageFormat;
}
