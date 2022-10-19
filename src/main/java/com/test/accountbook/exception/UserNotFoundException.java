package com.test.accountbook.exception;

public class UserNotFoundException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;

    public UserNotFoundException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

    public String getMessage(){
        return message;
    }
}
