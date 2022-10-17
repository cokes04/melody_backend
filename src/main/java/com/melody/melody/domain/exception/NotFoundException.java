package com.melody.melody.domain.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends DomainException{
    public NotFoundException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
