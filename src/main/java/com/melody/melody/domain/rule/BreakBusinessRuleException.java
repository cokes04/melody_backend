package com.melody.melody.domain.rule;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;

public class BreakBusinessRuleException extends DomainException {
    public BreakBusinessRuleException(DomainError... domainErrors) {
        super(domainErrors);
    }
}
