package com.melody.melody.domain.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class DomainException extends RuntimeException {
    private final DomainError[] domainErrors;

    public DomainException(DomainError... domainErrors) {
        super(
                Arrays.stream(domainErrors)
                        .map(DomainError::toString)
                        .reduce("", (rs, cu) -> rs + "\n" + cu)
                        .replaceFirst("\n", "")
        );

        this.domainErrors = domainErrors;
    }
}
