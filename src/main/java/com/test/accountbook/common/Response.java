package com.test.accountbook.common;

public class Response<T> {
    private final boolean success;
    private final T message;
    private final Error error;

    public Response(boolean success, T message, Error error){
        this.success = success;
        this.message = message;
        this.error = error;
    }

    public boolean isSuccess(){
        return success;
    }

    public T getMessage(){
        return message;
    }

    public Error getError(){
        return error;
    }

    public static <T> ResponseBuilder<T> builder(){
        return new ResponseBuilder<>();
    }

    public static class ResponseBuilder<T> {
        private boolean success;
        private T message;
        private Error error;

        private ResponseBuilder(){

        }

        public ResponseBuilder<T> success(boolean success){
            this.success = success;
            return this;
        }

        public ResponseBuilder<T> message(T message){
            this.message = message;
            return this;
        }

        public ResponseBuilder<T> error(Error error){
            this.error = error;
            return this;
        }

        public Response<T> build(){
            return new Response<T>(success, message, error);
        }
    }
}
