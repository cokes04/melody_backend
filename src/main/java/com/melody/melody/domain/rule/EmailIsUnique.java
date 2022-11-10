package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.UserErrorType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailIsUnique implements BusinessRule{

    private final UserRepository repository;
    private final String email;

    @Override
    public void check() {
        if (repository.existsByEmail(email))
            throw new BreakBusinessRuleException(
                    DomainError.of(UserErrorType.Email_Already_Used)
            );
    }
}
