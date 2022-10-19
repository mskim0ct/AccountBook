package com.test.accountbook.user.domain;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class SignInProperty {

    private LocalDateTime expiredAt;

    protected SignInProperty() {
    }

    protected SignInProperty(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    protected void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    protected LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
