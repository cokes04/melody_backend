package com.melody.melody.domain.exception;

import lombok.Value;

@Value
public class DomainError {
    private final DomainErrorType domainErrorType;
    private final Object[] arg;
    private final String message;

    private DomainError(DomainErrorType domainErrorType, Object[] arg, String message) {
        this.domainErrorType = domainErrorType;
        this.arg = arg;
        this.message = message;
    }

    public static DomainError of(DomainErrorType domainErrorType, Object... arg){
        String message = String.format(
                domainErrorType.getMessageFormat(), arg
        );

        return new DomainError(domainErrorType, arg, message);
    }
}
