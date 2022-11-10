package com.melody.melody.domain.rule;

import com.melody.melody.domain.exception.DomainError;

public interface BusinessRule {
    void check() throws BreakBusinessRuleException;
}
