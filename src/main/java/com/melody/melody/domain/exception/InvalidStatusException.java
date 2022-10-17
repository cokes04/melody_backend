package com.melody.melody.domain.exception;

public class InvalidStatusException extends DomainException{
    public InvalidStatusException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
