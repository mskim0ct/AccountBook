package com.test.accountbook.exception;

import com.test.accountbook.common.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.test.accountbook.common.Error;

@RestControllerAdvice
public class AccountBookExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, InvalidRequestException.class})
    public Response<Void> invalid(Exception exception){

        Error error;
        if(exception instanceof MethodArgumentNotValidException){
            error = new Error(ErrorCode.BAD_REQUEST, exception.getMessage());
        }else{
            InvalidRequestException invalidRequestException = (InvalidRequestException)exception;
            error = new Error(invalidRequestException.getErrorCode(), invalidRequestException.getMessage());
        }
        return Response.<Void>builder()
                .success(false)
                .message(null)
                .error(error)
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Response<Void> notFound(NotFoundException exception){
        Error error = new Error(exception.getErrorCode(), exception.getMessage());
        return Response.<Void>builder()
                .success(false)
                .message(null)
                .error(error)
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public Response<Void> conflict(DuplicateException duplicateException){
        Error error = new Error(duplicateException.getErrorCode(), duplicateException.getMessage());
        return Response.<Void>builder()
                .success(false)
                .message(null)
                .error(error)
                .build();
    }
}
