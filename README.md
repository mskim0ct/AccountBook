# account-book
본인의 소비내역을 기록/관리하는 백엔드

## 요구사항
### 사용자
- 고객은 이메일, 비밀번호 입력으로 회원가입
    - 비밀번호는 암호화하여 저장
- 고객은 로그인, 로그아웃 가능

### 가계부(고객)
#### 내역 기능
- 오늘 사용한 돈의 금액과 관련된 메모를 남길 수 있음
- 수정을 원하는 내역은 금액, 메모를 수정할 수 있음
- 삭제를 원하는 내역은 삭제 가능
    - 단건 내역 삭제 가능
    - 여러 내역 삭제 가능
- 삭제 내역은 언제든 복구 가능
    - 사용금액 날짜 기준으로 삭제 내역 복구 가능
    - 전체 삭제 내역 복구 가능
- 이제까지 기록한 가계부 리스트 볼 수 있음
    - 전체 내역 조회 가능
    - 시작날짜 종료날짜 기준으로 전체 내역 조회 가능
- 상세 내역을 볼 수 있음

#### 권한 기능
- 로그인하지 않은 고객은 가계부 내역에 대한 접근이 제한됨
  <br/>
  <br/>

## 설계
### ERD 설계
![erd.png](image/ERD.png)


### 테이블 설계
- users 테이블

| 속성               | 타입           | 설명     | 비고       |
|------------------|--------------|--------|----------|
| id               | bigint(20)   | 사용자 ID | PK       |
| email            | varchar(255) | 이메일    | Not Null |
| password         | varchar(255) | 패스워드   | Not Null |
| created_at       | datetime(6)  | 가입일    | Not Null |
| authority        | varchar(255) | 권한     | Null     |
| login_expired_at | datetime(6)  | 토큰 만료일 | Null     |

- account_records 테이블

| 속성         | 타입           | 설명            | 비고                          |
|------------|--------------|---------------|-----------------------------|
| id         | bigint(20)   | 가계부 내역 ID     | PK                          |
| money      | int          | 금액            | Not Null                    |
| memo       | varchar(255) | 메모            | Null                        |
| type       | varchar(255) | 입금/소비         | Not Null, default: 소비       |
| used_at    | date         | 소비 날짜         | Not Null, default: 내역 등록 날짜 |
| created_at | datetime(6)  | 내역 생성일        | Not Null                    |
| updated_at | datetime(6)  | 내역 수정일        | Not Null                    |
| deleted    | bit(1)       | 내역의 삭제여부      | Not Null, default: 0        | 
| user_id    | bigint(1)    | 가계부 내역 생성자 ID | Not Null                    |


## 구현
- 구현된 기능
    - 가계부
        - 가계부 내역 생성 구현 및 테스트 코드
        - 가계부 내역 수정 구현 및 테스트 코드
        - 가계부 내역 삭제(단건, 여러건) 구현 및 테스트 코드
        - 가계부 내역 복구(날짜 기준) 구현 및 테스트 코드
        - 가계부 내역 리스트 조회(날짜 기준) 구현 및 테스트 코드
        - 가계부 내역 상세 조회 구현 및 테스트 코드
    - 사용자
        - 회원가입 구현 및 테스트 코드
        - 로그인 (토큰발행)
        - 로그아웃


- 구현 못한 기능
    - 토큰 인증 문제 시 발생하는 Exception 통일
    - 로그인/로그아웃에 대한 Controller 테스트 코드

## API
### 가계부 내역
#### 내역 생성
POST /users/{userId}/account-records   
Content-Type: application/json   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI
```
{   
"money": 4000, //사용 금액: Integer   
"memo": "점심"  //메모: String
}
```
```
201

{
  "success": true,
  "message": null,
  "error": null
}
```

#### 내역 수정
PUT /users/{userId}/account-records/{accountRecordId}   
Content-Type: application/json   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI
```
{   
"money": 40000,   
"memo": "영화"   
}
```
```
200

{
  "success": true,
  "message": null,
  "error": null
}
```

#### 내역 삭제
DELETE /users/{userId}/account-records/{accountRecordId}   
Content-Type: application/json   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI

```

```
```
200

{
  "success": true,
  "message": null,
  "error": null
}
```


#### 삭제된 내역 복구
PATCH /users/{userId}/account-records/recover   
Content-Type: application/json   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI

※ localDate를 추가한 이유는 특정 시점 이후부터 복구하고 싶을 수 있다 생각하였고, 미입력 시 전체 복구되도록 구현하였습니다.
```
{
"localDate": "2022-10-14" //복구 기준 날짜: LocalDate('yyyy-MM-dd')
}
```
```
200

{
  "success": true,
  "message": null,
  "error": null
}
```

#### 내역 리스트 조회
GET /users/{userId}/account-records?startdate=2022-10-14&enddate=2022-10-15   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI

※ request param: startdate 는 내역 조회 시작날짜, enddate는 내역조회 끝날짜를 의미하며, 넣지 않을 경우, 전체 조회합니다.
```

```
```
200

{
  "success": true,
  "message": {
    "accountRecords": [
      {
        "accountRecordId": 1,
        "money": 1000,
        "memo": "점심",
        "usedAt": "2022-10-19"
      },
      {
        "accountRecordId": 2,
        "money": 2000,
        "memo": "커피",
        "usedAt": "2022-10-19"
      }
    ]
  },
  "error": null
}
```
#### 내역 상세 조회
GET /users/{userId}/account-records/{accountRecordId}   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI

```

```
```
200

{
  "success": true,
  "message": {
    null
  },
  "error": null
}
```
### 사용자
#### 회원가입
POST /sign-up

```
{
"email": "test@test.com", //이메일: String
"password": "test"  //비밀번호: String
}
```
```
201

{
  "success": true,
  "message": {
    null
  },
  "error": null
}
```

#### 로그인
POST /sign-in

```
{
"email": "test@test.com",
"password": "test"
}
```
```
200

{
  "success": true,
  "message": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTE5MzM5fQ.8DwW1kluLE3TQgjCL_7bbZhKphP2b7oJPhPaYhExPOI"
  },
  "error": null
}
```

#### 로그아웃
POST http://localhost:8080/logout   
Content-Type: application/json   
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNjY2MTkyNjY0fQ.pXlXyaXK-sCBb8rEXAC7snQgIKISp6kxkpxxahDRF0o
```
{}
```
```
{
  "success": true,
  "message": null,
  "error": null
}
```

## 패키지 구성
- 공통 패키지: com.test.accountbook
- 하위 패키지
    - accountrecord: 가계부 내역 관련 코드
    - common: 공통적으로 들어가는 기능 모아놓은 패키지
    - config: jwt 및 JPA, WebSecurity 설정을 모아놓은 패키지
    - exception: RuntimeException 발생 시, 에러 처리 로직 모아놓은 패키지
    - user: 사용자 관련 코드
- test 패키지
    - accountrecord: 가계부 내역 테스트 코드
    - user: 사용자 기능 테스트 코드

