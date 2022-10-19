package com.test.accountbook.user.controller;

import com.test.accountbook.exception.DuplicateException;
import com.test.accountbook.exception.ErrorCode;
import com.test.accountbook.user.service.UserAccountService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAccountController.class)
@ActiveProfiles("test")
class UserAccountControllerTest {

    private MockMvc mockMvc;
    @MockBean
    UserAccountService userAccountService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8"))
                .alwaysDo(print())
                .build();
    }

    @DisplayName("[회원가입] 성공")
    @ParameterizedTest
    @CsvSource("test@test.com,test")
    void signUp(String email, String password) throws Exception {
        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @DisplayName("[회원가입] 이메일 중복")
    @ParameterizedTest
    @CsvSource("test@test.com,test")
    void signUpEmailDuplication(String email, String password) throws Exception {
        //given
        willThrow(new DuplicateException(ErrorCode.EMAIL_DUPLICATED, ErrorCode.EMAIL_DUPLICATED.getMessage()))
                .given(userAccountService).signUp(any());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @DisplayName("[회원가입] 잘못된 입력")
    @ParameterizedTest
    @CsvSource({",test", "test@test.com,", ",", "test,"})
    void signUpInvalidValue(String email, String password) throws Exception {
        //given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        var body = jsonObject.toString();
        //when
        //then
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}