package com.melody.melody.adapter.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.melody.melody.domain.exception.DomainError;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime timestamp;

    private final List<Error> errors;

    public static ErrorResponse to(DomainError... domainErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errors(
                        Arrays.stream(domainErrors)
                        .map(Error::of)
                        .collect(Collectors.toList())
                )
                .build();
    }

    public static ErrorResponse to(String code, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errors(
                        List.of(
                                Error.builder()
                                        .code(code)
                                        .message(message)
                                        .build()
                        )
                )
                .build();
    }
    
    @Getter
    @Builder
    public static class Error{
        private final String code;
        private final String message;

        public static Error of(DomainError domainError){
            return Error.builder()
                    .code(domainError.getDomainErrorType().getCode())
                    .message(domainError.getMessage())
                    .build();
        }
    }

}