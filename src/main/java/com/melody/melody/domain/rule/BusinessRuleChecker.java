package com.melody.melody.domain.rule;

import com.melody.melody.domain.exception.DomainException;

import java.util.function.Supplier;

public interface BusinessRuleChecker {

    default void checkRule(BusinessRule rule){
        rule.check();
    }

    default void checkRule(BusinessRule rule, Supplier<DomainException> conversionException){
        try {
            rule.check();

        }catch (Exception e){
            throw conversionException.get();

        }
    }
}
