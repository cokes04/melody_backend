package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.CommonErrorType;
import lombok.Value;

import java.util.Objects;

@Value
public class Identity {
    private final long value;

    public static Identity from(long value) {
        if (value <= 0)
            throw new InvalidArgumentException(DomainError.of(CommonErrorType.Invalid_Identity));

        return new Identity(value);
    }

    public static Identity empty() {
        return new Identity(0L);
    }

    public boolean isEmpty(){
        return value == 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identity identity = (Identity) o;
        return value == identity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
