package com.test.accountbook.user.service;

import com.test.accountbook.common.CommonCode;
import com.test.accountbook.exception.AccountBookAuthenticationException;
import com.test.accountbook.exception.DuplicateException;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import com.test.accountbook.user.request.CreateUserAccountRequest;
import com.test.accountbook.user.request.SignInRequest;
import com.test.accountbook.user.response.SignInResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserAccountServiceTest {

    //회원가입: 성공, 이메일 중복
    //로그인: 성공, 잘못된 이메일, 잘못된 비번
    //로그아웃: signInExpiredAt null 확인

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @DisplayName("[회원가입] 성공")
    @ParameterizedTest
    @CsvSource({"test@test.com,test"})
    void signUp(String email, String password) {
        //given
        var createUserAccountRequest = new CreateUserAccountRequest(email, password);
        //when
        userAccountService.signUp(createUserAccountRequest);
        //then
        User user = userRepository.findByEmail(email).get();
        assertEquals(email, user.getEmail());
        assertEquals(CommonCode.AuthRole.USER.getValue(), user.getAuthority());
        assertNotNull(user.getCreatedAt());
    }

    @DisplayName("[회원가입] 이메일 중복")
    @Test
    void signUpEmailDuplicated() {
        //given
        var email = "test@test.com";
        var password = "test";
        userRepository.save(new User(email, passwordEncoder.encode(password)));
        var createUserAccountRequest = new CreateUserAccountRequest(email, password);
        //when
        //then
        DuplicateException duplicateException = assertThrows(DuplicateException.class, () ->
                userAccountService.signUp(createUserAccountRequest));
        assertEquals(ErrorCode.EMAIL_DUPLICATED, duplicateException.getErrorCode());
    }

    @DisplayName("[로그인] 성공")
    @Test
    void signIn() {
        //given
        var email = " test@test.com";
        var password = "test";
        userRepository.save(new User(email, passwordEncoder.encode(password)));
        SignInRequest signInRequest = new SignInRequest(email, password);
        //when
        SignInResponse signInResponse = userAccountService.signIn(signInRequest);
        //then
        assertNotNull(signInResponse.getToken());
        assertNotNull(userRepository.findByEmail(email).get().getSignInExpiredAt());
    }

    @DisplayName("[로그인] 잘못된 값 입력")
    @ParameterizedTest
    @CsvSource({"invalidTest@test.com,test", "test@test.com,invalidTest"})
    void signInFailed(String email, String password) {
        //given
        userRepository.save(new User("test@test.com", passwordEncoder.encode("test")));
        SignInRequest signInRequest = new SignInRequest(email, password);
        //when
        //then
        AccountBookAuthenticationException accountBookAuthenticationException =
                assertThrows(AccountBookAuthenticationException.class, () -> userAccountService.signIn(signInRequest));
        assertEquals(ErrorCode.SIGNIN_FAILED, accountBookAuthenticationException.getErrorCode());
    }

    @DisplayName("[로그아웃] 성공")
    @Test
    void logout() {
        //given
        var email = "test@test.com";
        var password = "test";
        var expiredDuration = 30;
        User user = userRepository.save(new User(email, passwordEncoder.encode(password)));
        user.signIn(expiredDuration);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority()))));
        //when
        userAccountService.logout();
        //then
        assertNull(userRepository.findByEmail(email).get().getSignInExpiredAt());
    }
}