package com.test.accountbook.accountrecord.service;

import com.test.accountbook.accountrecord.domain.AccountRecord;
import com.test.accountbook.accountrecord.domain.AccountRecordRepository;
import com.test.accountbook.accountrecord.request.CreateAccountRecordRequest;
import com.test.accountbook.accountrecord.request.RecoverAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.RetrieveAccountRecordsRequest;
import com.test.accountbook.accountrecord.request.UpdateAccountRecordRequest;
import com.test.accountbook.accountrecord.response.RetrieveAccountRecordsResponse;
import com.test.accountbook.accountrecord.response.RetrieveDetailedAccountRecordResponse;
import com.test.accountbook.common.CommonCode;
import com.test.accountbook.config.JpaConfiguration;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.exception.InvalidRequestException;
import com.test.accountbook.exception.NotFoundException;
import com.test.accountbook.user.domain.User;
import com.test.accountbook.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({JpaConfiguration.class})
@ActiveProfiles("test")
class AccountRecordServiceTest {

    private AccountRecordService accountRecordService;

    @Autowired
    private AccountRecordRepository accountRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        initDataRepository();
        accountRecordService = new AccountRecordService(accountRecordRepository,
                userRepository);
        testUser = userRepository.save(new User("test@test.com", "PASSWORD"));
    }

    private void initDataRepository() {
        accountRecordRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("[?????? ??????] ??????")
    @CsvSource({"10000,?????????", "20000,"})
    @ParameterizedTest
    void createAccountRecord(int money, String memo) {
        //given
        var createAccountRecordRequest = new CreateAccountRecordRequest(money, memo);
        var userId = testUser.getId();
        //when
        accountRecordService.createAccountRecord(userId, createAccountRecordRequest);
        //then
        var resultAccountRecord = accountRecordRepository.findByUserId(userId).get(0);
        assertEquals(userId, resultAccountRecord.getUserId());
        assertEquals(money, resultAccountRecord.getMoney());
        assertEquals(memo, resultAccountRecord.getMemo());
        assertEquals(CommonCode.AccountRecordType.WITHDRAW.value(), resultAccountRecord.getType());
        assertNotNull(resultAccountRecord.getUsedAt());
        assertNotNull(resultAccountRecord.getCreatedAt());
        assertNotNull(resultAccountRecord.getUpdatedAt());
        assertFalse(resultAccountRecord.isDeleted());
    }

    @DisplayName("[?????? ??????] ?????? ????????? ??????")
    @CsvSource({"10000,?????????", "20000,"})
    @ParameterizedTest
    void createAccountRecordNotFoundUser(int money, String memo) {
        //given
        var createAccountRecordRequest = new CreateAccountRecordRequest(money, memo);
        var userId = testUser.getId() + 1;
        //when
        //then
        NotFoundException userNotFoundException = assertThrows(NotFoundException.class, () ->
                accountRecordService.createAccountRecord(userId, createAccountRecordRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND, userNotFoundException.getErrorCode());
    }

    @DisplayName("[?????? ??????] ??????")
    @ParameterizedTest
    @CsvSource({"40000,?????????", "20000,", ",??????"})
    void updateAccountRecord(Integer money, String memo) {
        //given
        var initMoney = 10000;
        var initMemo = "????????? ??????";
        var accountRecordId = accountRecordRepository.save(new AccountRecord(initMoney, initMemo, testUser)).getId();
        var updateAccountRecordRequest = new UpdateAccountRecordRequest(money, memo);
        var userId = testUser.getId();
        //when
        accountRecordService.updateAccountRecord(userId, accountRecordId, updateAccountRecordRequest);
        //then
        AccountRecord accountRecord = accountRecordRepository.findByIdAndUserId(accountRecordId, userId).get();
        assertEquals(money == null ? initMoney : money, accountRecord.getMoney());
        assertEquals(memo == null ? initMemo : memo, accountRecord.getMemo());
        assertTrue(accountRecord.getCreatedAt().isBefore(accountRecord.getUpdatedAt()));
    }

    @DisplayName("[?????? ??????] ??????")
    @Test
    void deleteAccountRecord() {
        //given
        var accountId = accountRecordRepository.save(new AccountRecord(40000, "?????????", testUser)).getId();
        var userId = testUser.getId();
        //when
        accountRecordService.deleteAccountRecord(userId, accountId);
        //then
        assertTrue(accountRecordRepository.findByIdAndUserId(accountId, userId).get().isDeleted());
    }

    @DisplayName("[?????? ?????? ??????] ??????")
    @Test
    void deleteAccountRecords() {
        //given
        List<Long> accountRecordIds = new ArrayList<>();
        for (String[] value : new String[][]{new String[]{"5000", "??????"}, new String[]{"6000", "??????"}, new String[]{"5000", "??????"}}) {
            AccountRecord accountRecord = accountRecordRepository.save(new AccountRecord(Integer.parseInt(value[0]), value[1], testUser));
            accountRecordIds.add(accountRecord.getId());
        }
        var userId = testUser.getId();
        //when
        accountRecordService.deleteAccountRecords(userId, List.of(accountRecordIds.get(0), accountRecordIds.get(1))); //1,2????????? ??????
        //then
        assertTrue(accountRecordRepository.findByIdAndUserId(accountRecordIds.get(0), userId).get().isDeleted());
        assertTrue(accountRecordRepository.findByIdAndUserId(accountRecordIds.get(1), userId).get().isDeleted());
        assertFalse(accountRecordRepository.findByIdAndUserId(accountRecordIds.get(2), userId).get().isDeleted());
    }

    @DisplayName("[?????? ?????? ??????] ??????")
    @ParameterizedTest
    @CsvSource(value = {"null", "2022-10-14"}, nullValues = "null")
    void recoverAccountRecords(LocalDate localDate) {
        //given
        List<Long> accountRecordIds = initDeletedAccountRecords(List.of(LocalDate.of(2022, 10, 13),
                LocalDate.of(2022, 10, 14), LocalDate.of(2022, 10, 15)));
        RecoverAccountRecordsRequest recoverAccountRecordsRequest = new RecoverAccountRecordsRequest(localDate);
        var userId = testUser.getId();
        //when
        accountRecordService.recoverAccountRecords(userId, recoverAccountRecordsRequest);
        //then
        for (Long accountRecordId : accountRecordIds) {
            AccountRecord accountRecord = accountRecordRepository.findByIdAndUserId(accountRecordId, userId).get();
            // ?????? ?????? ??????(localDate)?????? ?????? ????????? ?????? ?????? ??????
            // ?????? ?????? ????????? ???, ?????? ?????? ??????
            assertTrue(localDate == null ||
                    localDate.isBefore(accountRecord.getUsedAt()) ||
                    localDate.isEqual(accountRecord.getUsedAt())
                            != accountRecord.isDeleted());
        }
    }

    private List<Long> initDeletedAccountRecords(List<LocalDate> localDates) {
        List<Long> accountRecordIds = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            AccountRecord accountRecord = new AccountRecord(5000, "??????", testUser, localDate).delete();
            accountRecordIds.add(accountRecordRepository.save(accountRecord).getId());
        }
        return accountRecordIds;
    }

    @DisplayName("[?????? ?????? ??????] ??????")
    @Test
    void retrieveDetailedAccountRecord() {
        //given
        int money = 40000;
        String memo = "?????????";
        long accountRecordId = accountRecordRepository.save(new AccountRecord(money, memo, testUser)).getId();
        long userId = testUser.getId();
        //when
        RetrieveDetailedAccountRecordResponse retrieveDetailedAccountRecordResponse =
                accountRecordService.retrieveDetailedAccountRecord(userId, accountRecordId);
        //then
        assertEquals(accountRecordId, retrieveDetailedAccountRecordResponse.getAccountRecordId());
        assertEquals(money, retrieveDetailedAccountRecordResponse.getMoney());
        assertEquals(memo, retrieveDetailedAccountRecordResponse.getMemo());
        assertNotNull(retrieveDetailedAccountRecordResponse.getUsedAt());
        assertNotNull(retrieveDetailedAccountRecordResponse.getCreatedAt());
        assertNotNull(retrieveDetailedAccountRecordResponse.getUpdatedAt());
    }

    @DisplayName("[?????? ?????? ??????] ???????????? ?????? ?????? ??????")
    @Test
    void retrieveDetailedAccountRecordNotFound() {
        //given
        var userId = testUser.getId();
        var accountRecordId = 10;
        //when
        //then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                accountRecordService.retrieveDetailedAccountRecord(userId, accountRecordId));
        assertEquals(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, notFoundException.getErrorCode());
    }

    @DisplayName("[?????? ????????? ??????] ??????")
    @ParameterizedTest
    @CsvSource({",,3", "2022-10-14,,2", "2022-10-13,,3", "2022-10-13,2022-10-14,2", ",2022-10-15,3", "2022-10-14,2022-10-14,1"})
    void retrieveAccountRecords(LocalDate startDate, LocalDate endDate, int result) {
        //given
        initEvenIndxDeletedAccountRecords(List.of(LocalDate.of(2022, 10, 12),
                LocalDate.of(2022, 10, 13),
                LocalDate.of(2022, 10, 13),
                LocalDate.of(2022, 10, 14),
                LocalDate.of(2022, 10, 15),
                LocalDate.of(2022, 10, 15)));
        long userId = testUser.getId();
        RetrieveAccountRecordsRequest retrieveAccountRecordsRequest = new RetrieveAccountRecordsRequest(startDate, endDate);
        //when
        RetrieveAccountRecordsResponse retrieveAccountRecordsResponse =
                accountRecordService.retrieveAccountRecords(userId, retrieveAccountRecordsRequest);
        //then
        List<RetrieveAccountRecordsResponse.AccountRecord> accountRecords = retrieveAccountRecordsResponse.getAccountRecords();
        assertEquals(result, accountRecords.size());
        for (RetrieveAccountRecordsResponse.AccountRecord accountRecord : accountRecords) {
            AccountRecord dbAccountRecord
                    = accountRecordRepository.findById(accountRecord.getAccountRecordId()).get();
            //????????? ?????? X
            assertFalse(dbAccountRecord.isDeleted());
            //????????? ?????? ??????, ??? ????????? ??????????????? ???
            if (startDate != null) {
                assertFalse(startDate.isAfter(accountRecord.getUsedAt()));
            }
            if (endDate != null) {
                assertFalse(endDate.isBefore(accountRecord.getUsedAt()));
            }
        }
    }

    @DisplayName("[?????? ????????? ??????] ??? ????????? ?????? ???????????? ?????? ???")
    @ParameterizedTest
    @CsvSource({"2022-10-14,2022-10-13"})
    void retrieveAccountRecordsInvalidDate(LocalDate startDate, LocalDate endDate) {
        //given
        long userId = testUser.getId();
        RetrieveAccountRecordsRequest retrieveAccountRecordsRequest = new RetrieveAccountRecordsRequest(startDate, endDate);
        //when
        //then
        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class, () ->
                accountRecordService.retrieveAccountRecords(userId, retrieveAccountRecordsRequest));
        assertEquals(ErrorCode.STARTDATE_IS_AFTER_ENDDATE, invalidRequestException.getErrorCode());
    }

    private void initEvenIndxDeletedAccountRecords(List<LocalDate> localDates) {
        for (int i = 0; i < localDates.size(); i++) {
            AccountRecord accountRecord =
                    accountRecordRepository.save(new AccountRecord(4000 + i * 100, "?????????" + i, testUser, localDates.get(i)));
            if (i % 2 == 0) { //?????? ????????? accountRecord ??? ?????? ??????
                accountRecord.delete();
            }
        }
    }

}