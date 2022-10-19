package com.test.accountbook.common;

import com.test.accountbook.exception.ErrorCode;

public class Error {
    private final ErrorCode errorCode;
    private final String message;

    public Error(ErrorCode errorCode, String message){
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
