package com.test.accountbook.config.jwt;

import com.test.accountbook.user.domain.CustomUserDetails;
import com.test.accountbook.exception.AccountBookAuthenticationException;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private final UserRepository userRepository;
    private final JwtProperty jwtProperty;
    private static final String BEARER = "Bearer";
    private final JwtUtils jwtUtils;

    public JwtProvider(UserRepository userRepository, JwtProperty jwtProperty) {
        this.userRepository = userRepository;
        this.jwtProperty = jwtProperty;
        jwtUtils = new JwtUtils();
    }

    public boolean validateToken(String headerToken) {
        //Bearer 타입
        if (headerToken == null || !headerToken.startsWith(BEARER + " ")) {
            return false;
        }
        //토큰 만료기한 확인
        Claims claims = jwtUtils.parseJws(getToken(headerToken), jwtProperty.getSecretKey());
        if (claims.getExpiration().before(new Date())) {
            return false;
        }
        //이미 로그아웃된 토큰
        CustomUserDetails customUserDetails = (CustomUserDetails) getUserDetails(claims.getSubject());
        return !(customUserDetails.getExpiredAt() == null);

    }

    private String getToken(String headerToken) {
        return headerToken.split(" ")[1];
    }

    public Authentication getAuthentication(String headerToken) {
        String token = getToken(headerToken);
        Claims claims = jwtUtils.parseJws(token, jwtProperty.getSecretKey());
        UserDetails userDetails = getUserDetails(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    private UserDetails getUserDetails(String email) {
        //UserDetailsService 구현
        UserDetailsService userDetailsService = username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() ->
                            new AccountBookAuthenticationException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
            return new CustomUserDetails(user);
        };

        return userDetailsService.loadUserByUsername(email);
    }

}
