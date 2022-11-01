package com.melody.melody.adapter.aws;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;

public class FailedFileUploadException extends DomainException {
    public FailedFileUploadException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
