package com.test.accountbook.accountrecord.request;

import javax.validation.constraints.NotNull;

public class CreateAccountRecordRequest {

    @NotNull
    private Integer money;
    private String memo;

    public CreateAccountRecordRequest(){

    }
    public CreateAccountRecordRequest(Integer money, String memo) {
        this.money = money;
        this.memo = memo;
    }

    public Integer getMoney() {
        return money;
    }

    public String getMemo() {
        return memo;
    }
}
