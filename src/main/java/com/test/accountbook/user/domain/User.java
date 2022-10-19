package com.test.accountbook.user.domain;

import com.test.accountbook.common.CommonCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "authority")
    private String authority;

    @Embedded
    @AttributeOverride(name = "expiredAt", column = @Column(name = "login_expired_at"))
    private SignInProperty signInProperty;

    protected User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.authority = CommonCode.AuthRole.USER.getValue();
        signInProperty = new SignInProperty();
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAuthority() {
        return authority;
    }

    public LocalDateTime getSignInExpiredAt() {
        return signInProperty == null ? null : signInProperty.getExpiredAt();
    }

    public void signIn(int expiredDuration) {
        signInProperty = new SignInProperty(LocalDateTime.now().plusMinutes(expiredDuration));
    }

    public void logout() {
        signInProperty = new SignInProperty();
    }
}
