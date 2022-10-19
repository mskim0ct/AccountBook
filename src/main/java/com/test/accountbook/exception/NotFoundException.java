package com.test.accountbook.exception;

public class NotFoundException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String message;
    public NotFoundException(ErrorCode errorCode, String message) {
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
