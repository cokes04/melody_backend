package com.melody.melody.domain.model;

import com.melody.melody.domain.event.Events;
import com.melody.melody.application.port.out.PasswordEncrypter;
import com.melody.melody.domain.event.UserWithdrew;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.rule.PasswordMatches;
import io.netty.util.internal.StringUtil;
import lombok.*;

import java.util.Optional;
import java.util.regex.Pattern;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Getter
public class User {
    private Identity id;

    private NickName nickName;

    private Email email;

    private Password password;

    private boolean withdrawn;

    public static User create(String nickName, String email, Password password){
        return User.builder()
                .id(Identity.empty())
                .nickName(NickName.from(nickName))
                .email(Email.from(email))
                .password(password)
                .withdrawn(false)
                .build();
    }

    public void update(String nickName){
        if (isWithdrawn())
            throw new InvalidStatusException(DomainError.of(UserErrorType.User_Already_Withdawn_Status));

        this.nickName = NickName.from(nickName);
    }

    public void changePassword(PasswordEncrypter encrypter, String oldRawPassword, Password newPassword){
        if (isWithdrawn())
            throw new InvalidStatusException(DomainError.of(UserErrorType.User_Already_Withdawn_Status));

        new PasswordMatches(encrypter, oldRawPassword, this.password).check();

        this.password = newPassword;
    }

    public void withdraw(){
        if (isWithdrawn())
            throw new InvalidStatusException(DomainError.of(UserErrorType.User_Already_Withdawn_Status));

        this.withdrawn = true;
        Events.raise(new UserWithdrew(this.id.getValue()));
    }


    @Value
    public static class NickName {
        private static final int maxLength = 40;

        private final String value;

        public static NickName from(String nickName){
            if (StringUtil.isNullOrEmpty(nickName) || nickName.length() > maxLength)
                throw new InvalidArgumentException(DomainError.of(UserErrorType.NickName_Length_Limit_Exceeded));

            return new NickName(nickName);
        }
    }

    @Value
    public static class Email {
        private static final String EMAIL_FORMAT_REGEX = "^[\\w+-_.]+@\\w+\\.\\w+(\\.\\w{1,3})?";

        private final String value;

        public static Email from(String email){
            if(StringUtil.isNullOrEmpty(email) || !Pattern.matches(EMAIL_FORMAT_REGEX, email) )
                throw new InvalidArgumentException(DomainError.of(UserErrorType.Invalid_Email_Format));

            return new Email(email);
        }
    }

    @Value
    public static class Password {
        private final String encryptedString;

        public static Password emptyPassword(){
            return new Password("");
        }
    }
}
