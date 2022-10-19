package com.test.accountbook.accountrecord.request;

import java.time.LocalDate;

public class RecoverAccountRecordsRequest {

    private LocalDate localDate;

    public RecoverAccountRecordsRequest(){

    }
    public RecoverAccountRecordsRequest(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDate getLocalDate(){
        return localDate;
    }
}
