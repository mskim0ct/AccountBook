package com.test.accountbook.user.response;

public class SignInResponse {
    private final String token;

    public SignInResponse(String token){
        this.token = token;
    }
    public String getToken(){
        return token;
    }
}
