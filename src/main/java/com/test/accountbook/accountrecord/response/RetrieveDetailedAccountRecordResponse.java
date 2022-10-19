package com.test.accountbook.accountrecord.response;

import com.test.accountbook.accountrecord.domain.AccountRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RetrieveDetailedAccountRecordResponse {

    private final long accountRecordId;
    private final Integer money;
    private final String memo;
    private final LocalDate usedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public RetrieveDetailedAccountRecordResponse(AccountRecord accountRecord){
        this.accountRecordId = accountRecord.getId();
        this.money = accountRecord.getMoney();
        this.memo = accountRecord.getMemo();
        this.usedAt = accountRecord.getUsedAt();
        this.createdAt = accountRecord.getCreatedAt();
        this.updatedAt = accountRecord.getUpdatedAt();
    }

    public long getAccountRecordId(){
        return accountRecordId;
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

}
