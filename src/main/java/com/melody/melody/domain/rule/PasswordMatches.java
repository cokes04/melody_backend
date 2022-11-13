package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.Password;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordMatches implements BusinessRule {

    private final PasswordEncrypter passwordEncrypter;
    private final String raw;
    private final Password encrypterd;

    @Override
    public void check() throws BreakBusinessRuleException{
        if (!passwordEncrypter.matches(raw, encrypterd))
            throw new BreakBusinessRuleException(
                    DomainError.of(UserErrorType.Passwod_Not_Matches)
            );
    }
}
