package com.melody.melody.domain.exception;

public class FailedAuthenticationException extends DomainException{
    public FailedAuthenticationException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
