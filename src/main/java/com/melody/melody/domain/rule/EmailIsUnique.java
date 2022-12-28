package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EmailIsUnique implements BusinessRule{

    private final UserRepository repository;
    private final User.Email email;

    public static EmailIsUnique create(UserRepository repository, User.Email email){
        return new EmailIsUnique(repository, email);
    }

    @Override
    public void check() {
        if (repository.existsByEmail(email))
            throw new BreakBusinessRuleException(
                    DomainError.of(UserErrorType.Email_Already_Used)
            );
    }
}
