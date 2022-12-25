package com.melody.melody.adapter.web.exception;

import com.melody.melody.domain.exception.*;
import com.melody.melody.domain.exception.type.UserErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    ResponseEntity<?> handle(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.to(ex.getDomainErrors())
                );
    }

    @ExceptionHandler(value = {InvalidStatusException.class})
    ResponseEntity<?> handle(InvalidStatusException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.to(ex.getDomainErrors())
                );
    }

    @ExceptionHandler(value = {FailedAuthenticationException.class})
    ResponseEntity<?> handle(FailedAuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.to(ex.getDomainErrors())
                );
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    ResponseEntity<?> handle(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.to(DomainError.of(UserErrorType.Authentication_Failed))
                );
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    ResponseEntity<?> handle(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.to(DomainError.of(UserErrorType.Not_Permission))
                );
    }

    @ExceptionHandler(value = {DomainException.class})
    ResponseEntity<?> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.to(ex.getDomainErrors())
                );
    }
}
