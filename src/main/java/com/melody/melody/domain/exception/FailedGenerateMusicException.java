package com.melody.melody.domain.exception;

public class FailedGenerateMusicException extends DomainException {
    public FailedGenerateMusicException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
