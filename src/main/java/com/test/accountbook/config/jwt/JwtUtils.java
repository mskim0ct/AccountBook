package com.test.accountbook.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtUtils {

    //create Jwt
    public String createJwt(String email, LocalDateTime expiredAt, String secretKey) {
        Date dateExpiredAt = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant());
        return createJwt(email, dateExpiredAt, secretKey);
    }

    private String createJwt(String email, Date expiredAt, String secretKey) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiredAt)
                .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    //parse Jwt
    public Claims parseJws(String token, String secretKey) {
        Key key = getKey(secretKey);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();
    }

    private SecretKey getKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
