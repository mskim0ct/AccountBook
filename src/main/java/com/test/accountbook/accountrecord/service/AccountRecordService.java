package com.test.accountbook.accountrecord.service;

import com.test.accountbook.accountrecord.domain.AccountRecord;
import com.test.accountbook.accountrecord.domain.AccountRecordRepository;
import com.test.accountbook.accountrecord.request.CreateAccountRecordRequest;
import com.test.accountbook.accountrecord.request.RecoverAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.RetrieveAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.UpdateAccountRecordRequest;
import com.test.accountbook.accountrecord.response.RetrieveAccountRecordsResponse;
import com.test.accountbook.accountrecord.response.RetrieveDetailedAccountRecordResponse;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.exception.InvalidRequestException;
import com.test.accountbook.exception.NotFoundException;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountRecordService {

    private final AccountRecordRepository accountRecordRepository;
    private final UserRepository userRepository;

    public AccountRecordService(AccountRecordRepository accountRecordRepository,
                                UserRepository userRepository) {
        this.accountRecordRepository = accountRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createAccountRecord(long userId,
                                    CreateAccountRecordRequest createAccountRecordRequest) {
        //user
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //create
        AccountRecord accountRecord = new AccountRecord(createAccountRecordRequest.getMoney(),
                createAccountRecordRequest.getMemo(), user, null);
        accountRecordRepository.save(accountRecord);
    }

    @Transactional
    public void updateAccountRecord(long userId,
                                    long accountRecordId,
                                    UpdateAccountRecordRequest updateAccountRecordRequest) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //update
        accountRecordRepository.findByIdAndUserId(accountRecordId, userId)
                .map(accountRecord -> {
                    if (updateAccountRecordRequest.getMoney() != null) {
                        accountRecord.changeMoney(updateAccountRecordRequest.getMoney());
                    }
                    if (updateAccountRecordRequest.getMemo() != null) {
                        accountRecord.changeMemo(updateAccountRecordRequest.getMemo());
                    }
                    return accountRecord;
                }).orElseThrow(() ->
                        new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void deleteAccountRecord(long userId, long accountId) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //delete
        accountRecordRepository.findByIdAndUserId(accountId, userId)
                .map(AccountRecord::delete)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage()));
    }

    @Transactional
    public void deleteAccountRecords(long userId, List<Long> accountIds) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //delete
        accountIds.forEach(accountId ->
                accountRecordRepository.findByIdAndUserId(accountId, userId)
                        .map(AccountRecord::delete)
                        .orElseThrow(() ->
                                new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage()))
        );
    }

    @Transactional
    public void recoverAccountRecords(long userId, RecoverAccountRecordsRequest recoverAccountRecordsRequest) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //recover
        accountRecordRepository.findByUserIdAndDeleted(userId)
                .forEach(accountRecord -> {
                    LocalDate pivotLocalDate = recoverAccountRecordsRequest.getLocalDate();
                    if (accountRecord.checkRecoverable(pivotLocalDate)) {
                        accountRecord.recover();
                    }
                });
    }

    @Transactional(readOnly = true)
    public RetrieveDetailedAccountRecordResponse retrieveDetailedAccountRecord(long userId, long accountRecordId) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //retrieve
        return accountRecordRepository.findByIdAndUserId(accountRecordId, userId)
                .map(RetrieveDetailedAccountRecordResponse::new)
                .orElseThrow(() ->
                        new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage())
                );
    }

    @Transactional(readOnly = true)
    public RetrieveAccountRecordsResponse retrieveAccountRecords(long userId,
                                                                 RetrieveAccountRecordsRequest retrieveAccountRecordsRequest) {
        //user
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //checkValidDate
        if (retrieveAccountRecordsRequest.getStartDate() != null &&
                retrieveAccountRecordsRequest.getEndDate() != null &&
                retrieveAccountRecordsRequest.getStartDate().isAfter(retrieveAccountRecordsRequest.getEndDate())) {
            throw new InvalidRequestException(ErrorCode.STARTDATE_IS_AFTER_ENDDATE, ErrorCode.STARTDATE_IS_AFTER_ENDDATE.getMessage());
        }
        //retrieveAll
        List<RetrieveAccountRecordsResponse.AccountRecord> accountRecords =
                accountRecordRepository.findByUserIdAndActive(userId).stream()
                        .filter(accountRecord ->
                                accountRecord.isActiveAccountRecord(retrieveAccountRecordsRequest.getStartDate(),
                                        retrieveAccountRecordsRequest.getEndDate()))
                        .map(RetrieveAccountRecordsResponse.AccountRecord::new)
                        .collect(Collectors.toList());

        return new RetrieveAccountRecordsResponse(accountRecords);
    }
}
