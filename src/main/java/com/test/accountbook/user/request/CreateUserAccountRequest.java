package com.test.accountbook.user.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class CreateUserAccountRequest {

    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;

    public CreateUserAccountRequest(){

    }

    public CreateUserAccountRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
