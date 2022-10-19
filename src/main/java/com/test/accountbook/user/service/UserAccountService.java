package com.test.accountbook.user.service;

import com.test.accountbook.config.jwt.JwtProperty;
import com.test.accountbook.config.jwt.JwtUtils;
import com.test.accountbook.user.request.SignInRequest;
import com.test.accountbook.user.response.SignInResponse;
import com.test.accountbook.exception.AccountBookAuthenticationException;
import com.test.accountbook.exception.DuplicateException;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import com.test.accountbook.user.request.CreateUserAccountRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperty jwtProperty;
    private final JwtUtils jwtUtils;

    public UserAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProperty jwtProperty) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperty = jwtProperty;
        jwtUtils = new JwtUtils();
    }

    @Transactional
    public void signUp(CreateUserAccountRequest createUserAccountRequest) {

        userRepository.findByEmail(createUserAccountRequest.getEmail())
                .ifPresent(notUsed -> {
                    throw new DuplicateException(ErrorCode.EMAIL_DUPLICATED, ErrorCode.EMAIL_DUPLICATED.getMessage());
                });

        userRepository.save(new User(createUserAccountRequest.getEmail(),
                passwordEncoder.encode(createUserAccountRequest.getPassword())));
    }

    @Transactional
    public SignInResponse signIn(SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new AccountBookAuthenticationException(ErrorCode.SIGNIN_FAILED,
                        ErrorCode.SIGNIN_FAILED.getMessage()));
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new AccountBookAuthenticationException(ErrorCode.SIGNIN_FAILED, ErrorCode.SIGNIN_FAILED.getMessage());
        }
        user.signIn(jwtProperty.getExpiredDuration());
        return new SignInResponse(jwtUtils.createJwt(user.getEmail(), user.getSignInExpiredAt(), jwtProperty.getSecretKey()));
    }

    @Transactional
    public void logout() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountBookAuthenticationException(ErrorCode.LOGOUT_FAILED,
                        ErrorCode.LOGOUT_FAILED.getMessage()));
        user.logout();
    }

}
