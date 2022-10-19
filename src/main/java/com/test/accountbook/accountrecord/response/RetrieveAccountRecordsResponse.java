package com.test.accountbook.accountrecord.response;

import java.time.LocalDate;
import java.util.List;

public class RetrieveAccountRecordsResponse {

    private final List<AccountRecord> accountRecords;

    public RetrieveAccountRecordsResponse(List<AccountRecord> accountRecords){
        this.accountRecords = accountRecords;
    }

    public List<AccountRecord> getAccountRecords(){
        return accountRecords;
    }
    public static class AccountRecord{

        private final long accountRecordId;
        private final int money;
        private final String memo;
        private final LocalDate usedAt;

        public AccountRecord(com.test.accountbook.accountrecord.domain.AccountRecord dbAccountRecord){
            accountRecordId = dbAccountRecord.getId();
            money = dbAccountRecord.getMoney();
            memo = dbAccountRecord.getMemo();
            usedAt = dbAccountRecord.getUsedAt();
        }

        public long getAccountRecordId(){
            return accountRecordId;
        }

        public int getMoney(){
            return money;
        }

        public String getMemo(){
            return memo;
        }

        public LocalDate getUsedAt(){
            return usedAt;
        }
    }

}
