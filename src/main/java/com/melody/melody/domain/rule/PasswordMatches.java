package com.melody.melody.domain.rule;

import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PasswordMatches implements BusinessRule {

    private final PasswordEncrypter passwordEncrypter;
    private final String raw;
    private final User.Password encrypted;

    public static PasswordMatches create(PasswordEncrypter passwordEncrypter, String raw, User.Password encrypted){
        return new PasswordMatches(passwordEncrypter, raw, encrypted);
    }

    @Override
    public void check() throws BreakBusinessRuleException{
        if (!passwordEncrypter.matches(raw, encrypted))
            throw new BreakBusinessRuleException(
                    DomainError.of(UserErrorType.Passwod_Not_Matches)
            );
    }
}
