package com.test.accountbook.accountrecord.request;

import java.time.LocalDate;

public class RetrieveAccountRecordsRequest {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public RetrieveAccountRecordsRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public LocalDate getEndDate(){
        return endDate;
    }
}
