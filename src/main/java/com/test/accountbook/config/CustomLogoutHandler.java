package com.test.accountbook.config;

import com.test.accountbook.config.jwt.JwtProvider;
import com.test.accountbook.config.jwt.JwtUtils;
import com.test.accountbook.exception.AccountBookAuthenticationException;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.exception.NotFoundException;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    public CustomLogoutHandler(JwtProvider jwtProvider, UserRepository userRepository){
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //header Authorization 추출
        String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        //Token 검증
        if(headerToken != null && jwtProvider.validateToken(headerToken)){
            //logout logout expiredAt 초기화
             String email = (String) jwtProvider.getAuthentication(headerToken).getPrincipal();
             User user = userRepository.findByEmail(email)
                     .orElseThrow(()->new AccountBookAuthenticationException(
                             ErrorCode.LOGOUT_FAILED, ErrorCode.LOGOUT_FAILED.getMessage()
                     ));
             user.logout();
             userRepository.saveAndFlush(user);

        }

    }
}
