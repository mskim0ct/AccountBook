package com.test.accountbook.exception;

public enum ErrorCode {

    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    ACCOUNT_RECORD_NOT_FOUND("계좌 내역을 찾을 수 없습니다."),
    EMAIL_DUPLICATED("중복된 이메일입니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    STARTDATE_IS_AFTER_ENDDATE("끝 날짜가 시작 날짜보다 빠릅니다."),
    SIGNIN_FAILED("인증에 실패하였습니다."),
    LOGOUT_FAILED("로그아웃에 실패하였습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    ALREADY_LOGOUT("이미 로그아웃되었습니다.");
    private final String message;

    ErrorCode(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
