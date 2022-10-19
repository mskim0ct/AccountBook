package com.test.accountbook.user.controller;

import com.test.accountbook.common.Response;
import com.test.accountbook.user.request.CreateUserAccountRequest;
import com.test.accountbook.user.request.SignInRequest;
import com.test.accountbook.user.response.SignInResponse;
import com.test.accountbook.user.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public Response<Void> signUp(@Valid @RequestBody CreateUserAccountRequest createUserAccountRequest) {
        userAccountService.signUp(createUserAccountRequest);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @PostMapping("/sign-in")
    public Response<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse signInResponse = userAccountService.signIn(signInRequest);
        return Response.<SignInResponse>builder()
                .success(true)
                .message(signInResponse)
                .error(null)
                .build();
    }

}
