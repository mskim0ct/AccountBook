package com.test.accountbook.accountrecord.request;

public class UpdateAccountRecordRequest {

    private Integer money;
    private String memo;

    public UpdateAccountRecordRequest(){

    }

    public UpdateAccountRecordRequest(Integer money, String memo) {
        this.money = money;
        this.memo = memo;
    }

    public Integer getMoney(){
        return money;
    }

    public String getMemo(){
        return memo;
    }

}
