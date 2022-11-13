package com.melody.melody.domain.rule;

public interface BusinessRule {
    void check() throws BreakBusinessRuleException;
}
