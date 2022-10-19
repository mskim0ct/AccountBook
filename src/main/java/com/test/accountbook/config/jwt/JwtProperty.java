package com.test.accountbook.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "jwt")
@ConstructorBinding
public class JwtProperty {

    private final String secretKey;
    private final int expiredDuration;

    public JwtProperty(String secretKey, int expiredDuration){
        this.secretKey = secretKey;
        this.expiredDuration = expiredDuration;
    }

    public String getSecretKey(){
        return secretKey;
    }

    public int getExpiredDuration(){
        return expiredDuration;
    }
}
