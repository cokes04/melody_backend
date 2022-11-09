package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.EmailAlreadyUsedException;
import com.melody.melody.domain.exception.type.UserErrorType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailIsUnique implements BusinessRule{

    private final UserRepository repository;
    private final String email;

    @Override
    public void isComplied() {
        if (repository.existsByEmail(email))
            throw new EmailAlreadyUsedException(
                    DomainError.of(UserErrorType.Email_Already_Used)
            );
    }
}
