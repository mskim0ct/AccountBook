package com.test.accountbook.common;

public class CommonCode {
    public enum AccountRecordType{
        DEPOSIT("입금"),
        WITHDRAW("지출");

        private final String value;

        AccountRecordType(String value){
            this.value = value;
        }
        public String value(){
            return value;
        }
    }

    public enum AuthRole {
        USER("ROLE_USER");

        private final String value;
        AuthRole(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }
}
