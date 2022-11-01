package com.melody.melody.adapter.aws;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;

public class FailedGenerateMusicException extends DomainException {
    public FailedGenerateMusicException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
