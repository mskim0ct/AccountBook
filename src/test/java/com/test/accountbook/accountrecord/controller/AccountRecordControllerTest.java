package com.test.accountbook.accountrecord.controller;

import com.test.accountbook.accountrecord.domain.AccountRecord;
import com.test.accountbook.accountrecord.response.RetrieveAccountRecordsResponse;
import com.test.accountbook.accountrecord.response.RetrieveDetailedAccountRecordResponse;
import com.test.accountbook.accountrecord.service.AccountRecordService;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.exception.InvalidRequestException;
import com.test.accountbook.exception.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountRecordController.class)
@ActiveProfiles("test")
class AccountRecordControllerTest {

    private MockMvc mockMvc;
    private long userId;

    @MockBean
    private AccountRecordService accountRecordService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8"))
                .alwaysDo(print())
                .build();
        userId = 1;
    }

    @DisplayName("[?????? ??????] ??????")
    @ParameterizedTest
    @CsvSource({"40000,?????????", "5000,"})
    void createAccountRecord(Integer money, String memo) throws Exception {
        //given
        willDoNothing().given(accountRecordService).createAccountRecord(anyLong(), any());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("money", money);
        jsonObject.put("memo", memo);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(post("/users/{userId}/account-records", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @DisplayName("[?????? ??????] ?????? ????????? ??????")
    @ParameterizedTest
    @CsvSource({"4000,?????????", ",?????????"})
    void createAccountRecordInvalidMoney(String money, String memo) throws Exception {
        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memo", money);
        jsonObject.put("money", memo);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(post("/users/{userId}/account-records", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("[?????? ??????] ??????")
    @ParameterizedTest
    @CsvSource({",", "5000,", ",?????????", "5000,?????????"})
    void updateAccountRecord(Integer money, String memo) throws Exception {
        //given
        willDoNothing().given(accountRecordService).updateAccountRecord(anyLong(), anyLong(), any());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("money", money);
        jsonObject.put("memo", memo);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(put("/users/{userId}/account-records/{accountRecordId}", userId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @DisplayName("[?????? ??????] ????????? ????????? ???????????? ?????? ???")
    @ParameterizedTest
    @CsvSource({",", "5000,", ",?????????", "5000,?????????"})
    void updateAccountRecordNotFound(Integer money, String memo) throws Exception {
        //given
        willThrow(new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage()))
                .given(accountRecordService).updateAccountRecord(anyLong(), anyLong(), any());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("money", money);
        jsonObject.put("memo", memo);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(put("/users/{userId}/account-records/{accountRecordId}", userId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[?????? ??????] ????????? ????????? ?????? ?????????????????? ???")
    @ParameterizedTest
    @CsvSource({"?????????,??????"})
    void updateAccountRecordInvalidMoney(String money, String memo) throws Exception {
        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("money", money);
        jsonObject.put("memo", memo);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(put("/users/{userId}/account-records/{accountRecordId}", userId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("[?????? ??????] ??????")
    @Test
    void deleteAccountRecord() throws Exception {
        //given
        willDoNothing().given(accountRecordService).deleteAccountRecord(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/users/{userId}/account-records/{accountRecordId}", userId, 1))
                .andExpect(status().isOk());
    }

    @DisplayName("[?????? ??????] ????????? ???????????? ?????? ???")
    @Test
    void deleteAccountRecordNotFound() throws Exception {
        //given
        willThrow(new NotFoundException(ErrorCode.ACCOUNT_RECORD_NOT_FOUND, ErrorCode.ACCOUNT_RECORD_NOT_FOUND.getMessage()))
                .given(accountRecordService).deleteAccountRecord(anyLong(), anyLong());
        //when
        //then
        mockMvc.perform(delete("/users/{userId}/account-records/{accountRecordId}", userId, 1))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[?????? ?????? ??????] ??????")
    @ValueSource(strings = {"1,2,3,4", "3", ""})
    @ParameterizedTest
    void deleteAccountRecords(String accountRecordIds) throws Exception {
        //given
        willDoNothing().given(accountRecordService).deleteAccountRecords(anyLong(), any());
        //when
        //then
        mockMvc.perform(delete("/users/{userId}/account-records", userId)
                        .queryParam("account-record-ids", accountRecordIds))
                .andExpect(status().isOk());
    }

    @DisplayName("[?????? ??????] ??????")
    @ParameterizedTest
    @ValueSource(strings = {"2022-10-30", ""})
    void recoverAccountRecords(String strLocalDate) throws Exception {
        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("localDate", strLocalDate);
        var body = jsonObject.toString();
        willDoNothing().given(accountRecordService).recoverAccountRecords(anyLong(), any());
        //when
        //then
        mockMvc.perform(patch("/users/{userId}/account-records/recover", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @DisplayName("[?????? ????????? ??????] ??????")
    @ParameterizedTest
    @CsvSource({",", "2022-10-14,", ",2022-10-14", "2022-10-14,2022-10-15"})
    void retrieveAccountRecords(String startDate, String endDate) throws Exception {
        //given
        given(accountRecordService.retrieveAccountRecords(anyLong(), any()))
                .willReturn(new RetrieveAccountRecordsResponse(Collections.emptyList()));

        //when
        //then
        mockMvc.perform(get("/users/{userId}/account-records", userId)
                        .queryParam("startdate", startDate)
                        .queryParam("enddate", endDate))
                .andExpect(status().isOk());
    }

    @DisplayName("[?????? ????????? ??????] ?????? ?????? > ??? ??????")
    @ParameterizedTest
    @CsvSource({"2022-10-15,2022-10-14"})
    void retrieveAccountRecordsInvalidDate(String startDate, String endDate) throws Exception {
        //given
        willThrow(new InvalidRequestException(ErrorCode.STARTDATE_IS_AFTER_ENDDATE,
                ErrorCode.STARTDATE_IS_AFTER_ENDDATE.getMessage()))
                .given(accountRecordService).retrieveAccountRecords(anyLong(), any());

        //when
        //then
        mockMvc.perform(get("/users/{userId}/account-records", userId)
                        .queryParam("startdate", startDate)
                        .queryParam("enddate", endDate))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("[?????? ????????????] ??????")
    @Test
    void retrieveAccountRecord() throws Exception {
        //given

        given(accountRecordService.retrieveDetailedAccountRecord(anyLong(), anyLong()))
                .willReturn(new RetrieveDetailedAccountRecordResponse(new AccountRecord(null, null, null)));
        //when
        //then
        mockMvc.perform(get("/users/{userId}/account-records/{accountRecordId}", userId, 1))
                .andExpect(status().isOk());
    }
}