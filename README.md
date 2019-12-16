# Information Service

## 공통 요구 사항
### 사용자 인증
* oauth JWT 기반의 인증 구현
* 사용자 정보는 persistence store에 저장, 패스워드 암호화  

## 서비스 별 요구 사항
## 주택 금융 서비스
* 주택금융 공급현황 데이터(csv 파일)을 입력받아 저장하는 API
* 금융기관 목록 조회 API
* 년도 별 각 금융 기관의 지원금액 합계 출력 API
* 년도 별 지원금액이 가장 큰 금융 기관명을 출력하는 API 
* 금융기관의 각 년도 별 지원금액 평균 중 가장 작은 금액과 큰 금액을 조회하는 API
* 2018년 특정 월 ,특정 금융 기관의 금융지원 금액 예측 조회 API

## 설계
### 사용자 인증
#### 공통
* spring-boot-oauth2, spring-security를 기반으로 구현
* test 환경의 특성성 Authorization Server 와 Resource Server가 같은 package내에 존재
* JWT secret은 비대칭 key pair를 사용

#### 1. Authorization Server
#### 2. Resource Server


### 실행 방법
