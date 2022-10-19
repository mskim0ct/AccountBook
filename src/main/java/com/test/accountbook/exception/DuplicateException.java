package com.test.accountbook.exception;

public class DuplicateException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String message;

    public DuplicateException(ErrorCode errorCode, String message){
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
