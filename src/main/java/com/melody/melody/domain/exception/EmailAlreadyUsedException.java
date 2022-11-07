package com.melody.melody.domain.exception;

public class EmailAlreadyUsedException extends DomainException{
    public EmailAlreadyUsedException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
