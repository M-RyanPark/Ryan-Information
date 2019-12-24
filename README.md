# Information Service

## 공통 요구 사항
### 사용자 인증
* oauth JWT 기반의 인증 구현
* 사용자 정보는 persistence store에 저장, 패스워드 암호화  

## 서비스 별 요구 사항
## 주택 금융 서비스
* 주택금융 지원 금액 데이터(csv 파일)을 입력받아 저장하는 API
* 금융 기관 목록 조회 API
* 년도 별 각 금융 기관의 지원금액 합계 출력 API
* 년도 별 지원금액이 가장 큰 금융 기관명을 출력하는 API 
* 금융 기관의 각 년도 별 지원금액 평균 중 가장 작은 금액과 큰 금액을 조회하는 API
* 2018년 특정 월 ,특정 금융 기관의 금융지원 금액 예측 조회 API

## 설계

### 사용자 인증
#### 공통
* spring-boot-oauth2, spring-security를 기반으로 구현
* test 환경의 특성성 Authorization Server 와 Resource Server가 같은 package내에 존재
* JWT를 이용한 access, refresh token을 사용, JWT secret은 비대칭 key pair를 사용
#### 1. Authorization Server
* Authorization Server 에서 Rest Api를 통한 token 발급을 위하여 별도의 grant_type 정의 하여 사용
* OAuth2AuthenticationProcessingFilter, 수정하여  Authorization 헤더에 “Bearer Token”으로 입력 요청 시 토큰 재발급 (미구현)
#### 2. Resource Server
* /api/** : 요청은 USER 이상 권한을 소유해야 접근 가능
* /admin/** : 요청은 ADMIN 이상 권한을 소유해야 접근 가능
#### 3 Datasource
* H2 인메모리 DB를 이용하여 기동시 jpa가 ddl을 자동 실행하여 스키마를 생성 함. 종료 시 데이터는 소멸 


### 주택 금융
#### Entity
* 금융 기관 - 금융 기관 이름, 코드 
* 금융 기관 별 지원 금액 - 금융 기관 코드 , 년 , 월, 금액
#### 주택금융 지원 금액 데이터(csv 파일)을 입력받아 저장하는 API
* 입력 : CSV 파일 
* 구분자 수가 다른 비정상 행에 대한 validation
* 첫 행에서 금유기관을 읽어와 금융기관 entity 존재하지 않는 금융 기관을 추가
* 금융 기관의 열 번호를 인식하여 금융 기관 별 지원 금액 entity에 추가 
#### 금융 기관 목록 조회 API
* 처리 : 금융 기관 Entity 전체 대상 을 조회하여 출력 
* 출력 : 금유기관 Entity 리스트 
#### 년도 별 각 금융 기관의 지원금액 합계 출력 API
* 입력 : 년도
* 처리 : 지원 금액의 입력 년도 데이터를 조회 후 기관별로 group by 하여 합계 계산
* 출력 : 년도, 지원 합계, 금융 기관 별 지원금액 리스트 
#### 년도 별 지원금액이 가장 큰 금융 기관명을 출력하는 API
* 입력 : 년도 
* 처리 : 지원 금액의 입력 년도 데이터를 조회 후 기관별로 group by 하여 합계 가 max 인 금융 기관을 선정
* 출력 : 년도 , 금융 기관 이름
#### 금융 기관의 각 년도 별 지원금액 평균 중 가장 작은 금액과 큰 금액을 조회하는 API
* 입력 : 금융 기관
* 처리 : 지원 금액의 입력 기관 별 데이터를 조회 후 년도별로 group by 하여 지원금의 합계를 구한 후 min / max 값을 조회 
* 출력 : 년도, 년도별 지원 금액 합계의 mix, max
#### 2018년 특정 월 ,특정 금융 기관의 금융지원 금액 예측 조회 API (미구현)
* 입력 : 년도, 월, 금융 기관
* 처리 : 최근 3, 5, 10 년의 금융 기관 별 흐름 변동 추이와, 금융시장의 전체 추이를 비교하여 가중치를 부여하여 근사값을 추정 
* 출력 : 년도, 월, 금융 기관 코드, 금융 기관 이름, 예상 지원 금액 

## API 명세 

### 회원가입
* POST /sign/up HTTP/1.1
{
	"userId" : "test1"
	, "password" : "test1"
	, "confirmPassword" : "test1"
}
* curl -X POST http://localhost:8080/sign/up -H 'Content-Type: application/json' -d '{"userId" : "test1", "password" : "test1", "confirmPassword" : "test1"}'

### 로그인
* POST /sign/in HTTP/1.1
{
	"userId" : "test1"
	, "password" : "test1"
	, "confirmPassword" : "test1"
}
* curl -X POST http://localhost:8080/sign/in -H 'Content-Type: application/json' -d '{"userId" : "test1", "password" : "test1"}'


### /api/ 는 로그인, 회원가입을 통해 발급 받은 토큰을 Oauth Bearer Token 으로 전송


### 금융 지원 항목 등록 
* POST /api/bank/support/data HTTP/1.1
Content-Type: multipart/form-data
Authorization: Bearer [TOKEN_VALUE]
file : CSV 파일 
* curl -X POST http://localhost:8080/api/bank/support/data -H 'Content-Type: application/x-www-form-urlencoded' -F 'file=@{filePath}'


### 금융기관 리스트 조회
* GET /api/bank/list HTTP/1.1
Authorization: Bearer [TOKEN_VALUE]
* curl -X GET http://localhost:8080/api/bank/list -H 'Content-Type: application/json' -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaW5mb3JtYXRpb24tYXBpIl0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTc3MTg1MzcxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjIzZDg4YzY3LTJjNDEtNGU4NS1hNzY0LWVjMDQ1NWUwYmU4NyIsImNsaWVudF9pZCI6ImxvYW5fY2xpZW50In0.QQaIwhqdWIXS01yJnE3W0PXpf7JpjJcEauCKFZwmK32l5x6_5cXqrFFY1a4HlhP8TB7ErKxjNJegd0bzb5mrYM5E1BVd9XA3s5zVNnp11Q6sEGBTiLfuBtd-f6QWXVPM_Xnh3FUGm1RupTSyvyV-eiM6uv_2B6dG5RllXCjeWGqhwBGVeDCg-rMUM7eTR0maTcvGy6DIvyXpOFbsjsvZMrCS7-OHxEs8KAwL0tM_Vp47-MqcATG03VFS3OgZglBg899whwlOra983SIhFggg11Y-I0Ph09ZkTa1qZ8aW-_JtVPLyCEKTrslZ6Dac2nxUO3M_RAuvHBZAc_Ag_UJ8Ww'

### 연간 지원 통계 조회
* GET /api/bank/support/year/list HTTP/1.1
Authorization: Bearer [TOKEN_VALUE]
* curl -X GET http://localhost:8080/api/bank/support/year/list -H 'Content-Type: application/json' -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaW5mb3JtYXRpb24tYXBpIl0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTc3MTg1MzcxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjIzZDg4YzY3LTJjNDEtNGU4NS1hNzY0LWVjMDQ1NWUwYmU4NyIsImNsaWVudF9pZCI6ImxvYW5fY2xpZW50In0.QQaIwhqdWIXS01yJnE3W0PXpf7JpjJcEauCKFZwmK32l5x6_5cXqrFFY1a4HlhP8TB7ErKxjNJegd0bzb5mrYM5E1BVd9XA3s5zVNnp11Q6sEGBTiLfuBtd-f6QWXVPM_Xnh3FUGm1RupTSyvyV-eiM6uv_2B6dG5RllXCjeWGqhwBGVeDCg-rMUM7eTR0maTcvGy6DIvyXpOFbsjsvZMrCS7-OHxEs8KAwL0tM_Vp47-MqcATG03VFS3OgZglBg899whwlOra983SIhFggg11Y-I0Ph09ZkTa1qZ8aW-_JtVPLyCEKTrslZ6Dac2nxUO3M_RAuvHBZAc_Ag_UJ8Ww'

### 연간 최고 지원 기관 조회
* GET /api/bank/support/year/top/{년도} HTTP/1.1
Authorization: Bearer [TOKEN_VALUE]
* curl -X GET localhost:8080/api/bank/support/year/top/2015 -H 'Content-Type: application/json' -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaW5mb3JtYXRpb24tYXBpIl0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTc3MTg1MzcxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjIzZDg4YzY3LTJjNDEtNGU4NS1hNzY0LWVjMDQ1NWUwYmU4NyIsImNsaWVudF9pZCI6ImxvYW5fY2xpZW50In0.QQaIwhqdWIXS01yJnE3W0PXpf7JpjJcEauCKFZwmK32l5x6_5cXqrFFY1a4HlhP8TB7ErKxjNJegd0bzb5mrYM5E1BVd9XA3s5zVNnp11Q6sEGBTiLfuBtd-f6QWXVPM_Xnh3FUGm1RupTSyvyV-eiM6uv_2B6dG5RllXCjeWGqhwBGVeDCg-rMUM7eTR0maTcvGy6DIvyXpOFbsjsvZMrCS7-OHxEs8KAwL0tM_Vp47-MqcATG03VFS3OgZglBg899whwlOra983SIhFggg11Y-I0Ph09ZkTa1qZ8aW-_JtVPLyCEKTrslZ6Dac2nxUO3M_RAuvHBZAc_Ag_UJ8Ww'

### 은행 별 통계 조회
* GET /api/bank/support/stats/bank?name=은행명 HTTP/1.1
Authorization: Bearer [TOKEN_VALUE]
* curl -X GET http://localhost:8080/api/bank/list?name=%EC%8B%A0%ED%95%9C%EC%9D%80%ED%96%89 -H 'Content-Type: application/json' -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaW5mb3JtYXRpb24tYXBpIl0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTc3MTg1MzcxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjIzZDg4YzY3LTJjNDEtNGU4NS1hNzY0LWVjMDQ1NWUwYmU4NyIsImNsaWVudF9pZCI6ImxvYW5fY2xpZW50In0.QQaIwhqdWIXS01yJnE3W0PXpf7JpjJcEauCKFZwmK32l5x6_5cXqrFFY1a4HlhP8TB7ErKxjNJegd0bzb5mrYM5E1BVd9XA3s5zVNnp11Q6sEGBTiLfuBtd-f6QWXVPM_Xnh3FUGm1RupTSyvyV-eiM6uv_2B6dG5RllXCjeWGqhwBGVeDCg-rMUM7eTR0maTcvGy6DIvyXpOFbsjsvZMrCS7-OHxEs8KAwL0tM_Vp47-MqcATG03VFS3OgZglBg899whwlOra983SIhFggg11Y-I0Ph09ZkTa1qZ8aW-_JtVPLyCEKTrslZ6Dac2nxUO3M_RAuvHBZAc_Ag_UJ8Ww'




## 실행 방법 
* spring-boot run 실행
* java -jar information-0.0.1-SNAPSHOT.jar app.jar
* 
* docker 실행 
* docker pull sakukiller/information
* docker run -p 8080:8080 sakukiller/information


## Repository
* Github : https://github.com/M-RyanPark/Ryan-Information
* Jar Link : https://drive.google.com/file/d/11nz_yxNb1-OFLYtZmIzRw7qb0SS-Gh6m/view
