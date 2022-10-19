package com.test.accountbook.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountBookAuthenticationException extends AuthenticationException {

    private final ErrorCode errorCode;
    private final String message;

    public AccountBookAuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
