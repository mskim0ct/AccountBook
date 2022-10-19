package com.test.accountbook.accountrecord.domain;

import com.test.accountbook.common.CommonCode;
import com.test.accountbook.user.domain.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name="account_records")
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class AccountRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "money", nullable = false)
    private Integer money;
    @Column(name = "memo")
    private String memo;
    @Column(name = "used_at", nullable = false)
    private LocalDate usedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected AccountRecord(){

    }

    public AccountRecord(Integer money, String memo, User user, LocalDate usedAt){
        type = CommonCode.AccountRecordType.WITHDRAW.value();
        this.money = money;
        this.memo = memo;
        this.user = user;
        this.usedAt = usedAt == null ? LocalDate.now() : usedAt;
    }

    public AccountRecord(Integer money, String memo, User user) {
        this(money, memo, user, null);
    }

    public long getId(){
        return id;
    }

    public String getType(){
        return type;
    }

    public Integer getMoney(){
        return money;
    }

    public String getMemo(){
        return memo;
    }

    public LocalDate getUsedAt(){
        return usedAt;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    public boolean isDeleted(){
        return deleted;
    }

    public long getUserId(){
        return user.getId();
    }

    public AccountRecord changeMoney(Integer money){
        this.money = money;
        return this;
    }

    public AccountRecord changeMemo(String memo){
        this.memo = memo;
        return this;
    }

    public AccountRecord delete() {
        deleted = true;
        return this;
    }

    public boolean checkRecoverable(LocalDate pivotLocalDate){
        return pivotLocalDate == null || pivotLocalDate.isEqual(usedAt)
                || pivotLocalDate.isBefore(usedAt);
    }

    public void recover(){
        deleted = false;
    }

    public boolean isActiveAccountRecord(LocalDate pivotStartDate, LocalDate pivotEndDate) {
        boolean active = true;
        if(pivotStartDate != null && pivotStartDate.isAfter(usedAt) || //시작 날짜보다 전 내역
                pivotEndDate != null && pivotEndDate.isBefore(usedAt)){ //끝 날짜보다 이후 내역
            active = false;
        }
        return active;
    }
}
