package com.test.accountbook.user.domain;

import com.test.accountbook.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;
    private LocalDateTime expiredAt;
    private Collection<GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        email = user.getEmail();
        password = user.getPassword();
        expiredAt = user.getSignInExpiredAt();
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getAuthority()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public LocalDateTime getExpiredAt(){
        return expiredAt;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
