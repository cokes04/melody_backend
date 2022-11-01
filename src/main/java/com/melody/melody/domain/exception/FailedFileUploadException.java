package com.melody.melody.domain.exception;

public class FailedFileUploadException extends DomainException {
    public FailedFileUploadException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
