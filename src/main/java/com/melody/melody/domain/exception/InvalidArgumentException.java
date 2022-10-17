package com.melody.melody.domain.exception;

public class InvalidArgumentException extends DomainException{
    public InvalidArgumentException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
