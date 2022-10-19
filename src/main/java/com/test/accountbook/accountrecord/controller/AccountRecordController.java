package com.test.accountbook.accountrecord.controller;

import com.test.accountbook.accountrecord.request.CreateAccountRecordRequest;
import com.test.accountbook.accountrecord.request.RecoverAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.RetrieveAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.UpdateAccountRecordRequest;
import com.test.accountbook.accountrecord.response.RetrieveAccountRecordsResponse;
import com.test.accountbook.accountrecord.response.RetrieveDetailedAccountRecordResponse;
import com.test.accountbook.accountrecord.service.AccountRecordService;
import com.test.accountbook.common.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
public class AccountRecordController {

    private final AccountRecordService accountRecordService;

    public AccountRecordController(AccountRecordService accountRecordService) {
        this.accountRecordService = accountRecordService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/account-records")
    public Response<Void> createAccountRecord(@PathVariable("userId") Long userId,
                                              @Valid @RequestBody CreateAccountRecordRequest createAccountRecordRequest) {
        accountRecordService.createAccountRecord(userId, createAccountRecordRequest);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @PutMapping("/account-records/{accountRecordId}")
    public Response<Void> updateAccountRecord(@PathVariable("userId") Long userId,
                                              @PathVariable("accountRecordId") Long accountRecordId,
                                              @RequestBody UpdateAccountRecordRequest updateAccountRecordRequest) {
        accountRecordService.updateAccountRecord(userId, accountRecordId, updateAccountRecordRequest);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @DeleteMapping("/account-records/{accountRecordId}")
    public Response<Void> deleteAccountRecord(@PathVariable("userId") Long userId,
                                              @PathVariable("accountRecordId") Long accountRecordId) {
        accountRecordService.deleteAccountRecord(userId, accountRecordId);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @DeleteMapping("/account-records")
    public Response<Void> deleteAccountRecords(@PathVariable("userId") Long userId,
                                               @RequestParam(value = "account-record-ids", required = false) List<Long> accountRecordIds) {
        accountRecordService.deleteAccountRecords(userId, accountRecordIds);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @PatchMapping("/account-records/recover")
    public Response<Void> recoverAccountRecord(@PathVariable("userId") Long userId,
                                               @RequestBody RecoverAccountRecordsRequest recoverAccountRecordsRequest) {
        accountRecordService.recoverAccountRecords(userId, recoverAccountRecordsRequest);
        return Response.<Void>builder()
                .success(true)
                .message(null)
                .error(null)
                .build();
    }

    @GetMapping("/account-records")
    public Response<RetrieveAccountRecordsResponse> retrieveAccountRecords(@PathVariable("userId") Long userId,
                                                                           @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                           @RequestParam(value = "startdate", required = false) LocalDate startDate,
                                                                           @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                           @RequestParam(value = "enddate", required = false) LocalDate endDate) {
        RetrieveAccountRecordsRequest retrieveAccountRecordsRequest =
                new RetrieveAccountRecordsRequest(startDate, endDate);

        RetrieveAccountRecordsResponse retrieveAccountRecordsResponse =
                accountRecordService.retrieveAccountRecords(userId, retrieveAccountRecordsRequest);

        return Response.<RetrieveAccountRecordsResponse>builder()
                .success(true)
                .message(retrieveAccountRecordsResponse)
                .error(null)
                .build();
    }

    @GetMapping("/account-records/{accountRecordId}")
    public Response<RetrieveDetailedAccountRecordResponse> retrieveDetailedAccountRecord(
            @PathVariable("userId") Long userId,
            @PathVariable("accountRecordId") Long accountRecordId
    ){
        RetrieveDetailedAccountRecordResponse retrieveDetailedAccountRecordResponse =
                accountRecordService.retrieveDetailedAccountRecord(userId, accountRecordId);

        return Response.<RetrieveDetailedAccountRecordResponse>builder()
                .success(true)
                .message(retrieveDetailedAccountRecordResponse)
                .error(null)
                .build();
    }

}
