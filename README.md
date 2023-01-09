# 멜로디

# 목차 
 - [소개](#소개) 
 - [연관 저장소](#연관-저장소)
 - [사용 기술](#사용-기술)
 - [아키텍처](#아키텍처)
 - [다이어그램](#다이어그램)
 - [Api 문서](#-Api-문서)
 - [주요 구현](#주요-구현)
     - [작곡 기능](#작곡-기능)
     - [보안](#보안)
     - [캐싱](#캐싱)
     - [예외](#예외)
 
## 소개
멜로디는 사진의 분위기에 어울리는 음악을 작곡하고, 작곡된 음악을 공유할 수 있는 애플리케이션입니다.

+ 팀 구성  
FE2, BE1

+ 맡은 역할
  + 작곡 및 게시물, 유저 CRUD 기능, 인증/인가 등 서버 전체 구현
  + 인프라 설계 및 관리
  + 작곡 모델 개발 및 딥러닝 모델간의 통합
  
## 연관 저장소  
+ 안드로이드 애플리케이션 및 딥러닝 모델  
https://github.com/cokes04/melody
   
## 사용 기술
+ 백엔드
  + Java 11 openjdk
  + SpringBoot 2.7.4
  + Spring Security
  + Spring Data JPA
  + QueryDSL

+ 데이터베이스
  + MySQL

+ 인프라
  + AWS EC2
  + AWS S3
  + AWS Lambda
  + AWS SQS
  + AWS RDS
  + Docker

## 아키텍처

### 시스템 아키텍처
![시스템](https://user-images.githubusercontent.com/64632340/208399185-0f85d437-c6cb-453d-a36e-96d65f363613.png)

### WAS 아키텍처
헥사고날 아키텍처 [출처](https://commons.wikimedia.org/wiki/File:Hexagonal_Architecture.svg)  
![WAS](https://upload.wikimedia.org/wikipedia/commons/7/75/Hexagonal_Architecture.svg)  


## 다이어그램
###  E-R 다이어그램
![ERD](https://user-images.githubusercontent.com/64632340/208380163-0d416575-5c17-4171-af2e-6cfb46f52771.jpg)

### 패키지 다이어그램
![패키지](https://user-images.githubusercontent.com/64632340/208380344-a93bdf17-349f-45f7-8d18-7326f4daeb95.jpg)

### 클래스 다이어그램
![클래스](https://user-images.githubusercontent.com/64632340/211291207-d15ee9d1-44f4-4f91-a453-4463301abfb6.png)

## Api 문서
[Api 문서 보기](https://cokes04.github.io/melody_backend/docs)

## 주요 구현
### 작곡 기능
![작곡프로세스](https://user-images.githubusercontent.com/64632340/208419132-b089e8e7-64a5-4053-acb6-0732ff91dd18.png)

사진에 대한 음악을 작곡하기 위해서 이미지 캡셔닝 모델부터 감정 분류 모델, 작곡 모델을 순차적으로 거치면 최종적으로 악보 파일이 생성되고, 저장됩니다.  
각 딥러닝 모델은 서버 관리의 부담을 줄이고 비용 절감을 위해서 AWS Lambda를 이용하여 배포하였으며, 배포된 딥러닝 모델은 WAS에 의해서 중계되게 설계했습니다.  
또한 작곡 모델의 긴 작곡 시간을 해소하기 위해서 AWS API Gateway의 비동기 요청 기능을 이용했습니다.  
추후 클라이언트는 풀링을 통해서 작곡 완료를 확인할 수 있습니다.

[GenerateMusicService](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/application/service/music/GenerateMusicService.java)
1. 클라이언트는 WAS에 Multipart로 사진과 작곡에 필요한 데이터를 담아서 작곡 요청을 전송합니다.
2. 작곡 요청 받은 WAS는 사진 저장소에 전달 받은 사진을 저장하고, 사진의 URI를 반환 받습니다.
3. 그리고 사진의 URI를 이미지 캡셔닝 모델에 전달하여 사진에 대한 설명(캡션)을 추출하여 반환 받습니다.
4. 추출된 설명을 감정 분류 모델에 전달하여 설명에 대한 감정(분위기)를 분류하여 반환 받습니다.
5. 현재까지 얻은 결과물을 DB에 저장합니다.
6. 작곡 모델에 감정과 음악 아이디, 음악 길이, 노이즈를 비동기 요청으로 전달합니다.  
  6-1. 작곡 모델은 전달 받은 데이터로 작곡을 진행하여 얻은 악보 파일을 음악 저장소에 저장합니다.  
  6-2. 악보 파일을 저장 후 악보 파일의 URI와 음악 아이디를 페이로드로 하여 작곡 완료 큐에 메시지를 발행합니다.  
7. 작곡 모델에 비동기 요청을 전송하고, 클라이언트에 현재까지 얻은 결과물을 응답하여 클라이언트와의 통신 과정을 종료합니다.
8. 작곡 완료 큐를 구독하면서 발행된 메시지를 소비합니다.
9. 작곡 완료 메시지의 음악 아이디에 해당하는 작곡 정보의 작곡 상태를 완료로 변경하고, 악보 파일의 URI를 기록하여 작곡 과정을 종료합니다.

### 보안
#### 인증
사용자 인증을 위해서 이메일/패스워드를 통한 인증과 jwt 인증을 구현하였습니다.  
보안 및 로그인 유지를 위해서 access token의 유효 기간을 짧게 설정하고, refresh token을 활용하였습니다.  
refresh token의 탈취를 막기 위해서 DB에 사용자당 하나의 refresh token만 유지하고, 재발급이 이루어질때 기존의 refresh token과 전달 받은 refresh token이 일치해야 합니다.   
일치하면 새로운 access token과 refresh token을 발급하여 전달하고, 재발급된 refresh token을 DB에 저장하게 됩니다.  

[JwtAuthenticationFilter](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/web/security/JwtAuthenticationFilter.java)   
[JwtTokenIssuanceService](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/web/security/JwtTokenIssuanceService.java)   

이메일/패스워드를 통한 인증이 완료되면 access token과 refresh token을 발급하고, 발급된 access token은 Body에 담아서 전달되어 클라이언트의 메모리 변수에 저장되고, refresh token은 Cookie에 담겨서 전달됩니다.   
이렇게 발급된 acceses token은 요청 메시지의 Authorization 헤더에 담기고, refresh token은 Cookie에 담겨서 전달되어 JwtAuthenticationFilter에서 다음과 같은 인증 과정이 이루어집니다.  
+ 인증 과정
  + acceses token 검증
    + 유효 : 인증 성공
    
    + 존재 X || 유효 X : refresh token 검증
    
  + refresh token 검증
    + 유효 
      + 기존(DB)의 refresh token과 일치 : 인증 성공 && acceses token 및 refresh token 재발급 및 전달 && 발급한 refresh token 저장
      
      + 불일치 : 401 Unauthorized
      
    + 존재 X || 유효 X : 익명의 사용자

#### 인가
"게시물은 작성자 본인만 제거가 가능하다” 같은 경우에는 URL만으로 접근을 허용하는 것을 결정하는 것을 어렵기 때문에 게시물을 DB에서 조회하여 본인 확인을 하는 보안 로직이 필요합니다.  
이러한 보안 로직을 표현하는 SpEL 표현식을 정의하고, PreAuthorize, PostAuthorize 어노테이션을 통해서 비즈니스 로직과 보안 로직을 분리하여 서비스 객체의 메서드 단위로 접근 권한을 확인 하는 기능을 수행할 수 있도록 하였습니다.  
```
@PreAuthorize("#post.isOwner(#command.postId)")
    public Result execute(Command command) {
        ~~~
}
```
[MusicSecurityExpression](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/security/MusicSecurityExpression.java)  
[PostSecurityExpression](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/security/PostSecurityExpression.java)  
[UserSecurityExpression](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/security/UserSecurityExpression.java)  

### 캐싱
성능 개선을 위해서 Caffeine Cache를 사용하여 캐시를 적용하였습니다.    
매 요청마다 게시물 페이징 쿼리를 DB의 큰 부하가 발생할 것이라고 판단하여 캐시에 게시글의 총 사이즈 및 페이징 관련된 정보를 유지하면 DB의 부하를 줄임과 동시에 빠른 응답이 가능할 것이라고 생각하여 적용하였습니다.    
그리고 데이터의 정합성이 깨지는 메서드를 호출할 경우 캐시에서 데이터를 폐기하여 정합성을 유지하였습니다.   

[캐시 종류 Enum](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/config/CacheType.java)   
[캐시 설정](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/config/CacheConfig.java)   

### 예외
에러 발생을 처리하기 위해서 에러 발생 원인에 따라서 예외 클래스를 정의하고, 상세한 예외 메시지를 Enum으로 관리하였습니다.  
발생한 예외는 ExceptionHandler를 통해서 적절한 HTTP 상태 코드와 공통된 포맷의 예외 메시지로 변환하여 응답합니다.   
[예외 패키지](https://github.com/cokes04/melody_backend/tree/main/src/main/java/com/melody/melody/domain/exception)  
[GlobalExceptionHandler](https://github.com/cokes04/melody_backend/blob/main/src/main/java/com/melody/melody/adapter/web/exception/GlobalExceptionHandler.java)  