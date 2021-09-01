# querydsl_inf
인프런 실전! Querydsl 김영한님 강의 정리 Repo


# 목차
* Querydsl 소개
  * 소개
  * 강의 자료

* 프로젝트 환경설정
  * 프로젝트 생성

  * Querydsl 설정과 검증

  * 라이브러리 살펴보기

  * H2 데이터베이스 설치

  * 스프링 부트 설정 - JPA, DB

* 예제 도메인 모델
  * 예제 도메인 모델과 동작확인

* 기본 문법시작 - JPQL vs Querydsl
  * 기본 Q-Type 활용
  
  * 검색 조건 쿼리

  * 결과 조회

  * 정렬

  * 페이징

  * 집합

  * 조인 - 기본 조인

  * 조인 - on절

  * 조인 - 페치 조인

  * 서브 쿼리

  * Case 문

  * 상수, 문자 더하기

* 중급 문법

  * 프로젝션과 결과 반환 - 기본

  * 프로젝션과 결과 반환 - DTO 조회

  * 프로젝션과 결과 반환 - @QueryProjection

  * 동적 쿼리 - BooleanBuilder 사용

  * 동적 쿼리 - Where 다중 파라미터 사용

  * 수정, 삭제 벌크 연산

* SQL function 호출하기
  * 실무 활용 - 순수 JPA와 Querydsl
  * 순수 JPA 리포지토리와 Querydsl

  * 동적 쿼리와 성능 최적화 조회 - Builder 사용

  * 동적 쿼리와 성능 최적화 조회 - Where절 파라미터 사용

  * 조회 API 컨트롤러 개발

* 실무 활용 - 스프링 데이터 JPA와 Querydsl
  * 스프링 데이터 JPA 리포지토리로 변경
  * 사용자 정의 리포지토리

  * 스프링 데이터 페이징 활용1 - Querydsl 페이징 연동

  * 스프링 데이터 페이징 활용2 - CountQuery 최적화

  * 스프링 데이터 페이징 활용3 - 컨트롤러 개발

* 스프링 데이터 JPA가 제공하는 Querydsl 기능
  * 인터페이스 지원 - QuerydslPredicateExecutor

  * Querydsl Web 지원

  * 리포지토리 지원 - QuerydslRepositorySupport

  * Querydsl 지원 클래스 직접 만들기

# 프로젝트 생성
* 스프링 부트 스타터(https://start.spring.io/)
  * 사용 기능: Spring Web, jpa, h2, lombok
  * querydsl은 별도로 디펜던시 추가 


## IntelliJ Gradle 대신에 자바로 바로 실행하기
* 최근 IntelliJ 버전은 Gradle로 실행을 하는 것이 기본 설정이다. 이렇게 하면 실행속도가 느리다. 다음과
  같이 변경하면 자바로 바로 실행하므로 좀 더 빨라진다.

1. 1references Build, Execution, Deployment Build Tools Gradle
2. Build and run using: Gradle IntelliJ IDEA
3. Run tests using: Gradle IntelliJ IDEA

```kotlin
# build.gradle 에 주석을 참고해서 querydsl 설정 추가
plugins {
  id 'org.springframework.boot' version ‘2.2.2.RELEASE'
  id 'io.spring.dependency-management' version '1.0.8.RELEASE'

  //querydsl 추가
  id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
  id 'java'
}

...

dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  implementation 'org.springframework.boot:spring-boot-starter-web'
  //querydsl 추가
  implementation 'com.querydsl:querydsl-jpa'
  //
  compileOnly 'org.projectlombok:lombok'
  runtimeOnly 'com.h2database:h2'
  annotationProcessor 'org.projectlombok:lombok'
  testImplementation('org.springframework.boot:spring-boot-starter-test') {
  exclude group: ‘org.junit.vintage’, module: ‘junit-vintage-engine'
  }
}

//querydsl 추가 시작
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}

sourceSets {
    main.java.srcDir querydslDir
}

configurations {
    querydsl.extendsFrom compileClasspath
}

compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
//querydsl 추가 끝
```

## 빌드하여 querydsl용 QType Entity 생성하는법

검증용 Q 타입 생성
* querydsl이 @Entity 를 보고 자동으로 생성해준다. 

* Gradle IntelliJ 사용법 (인텔리제이 오른쪽 gradle에서 사용하면 된다. )
  * Gradle Tasks build clean
  * Gradle Tasks other compileQuerydsl

* Gradle 콘솔 사용법
  * ./gradlew clean compileQuerydsl

* Q 타입 생성 확인
  * build -> generated -> querydsl
    * study.querydsl.entity.QHello.java 파일이 생성되어 있어야 함
    * 빌드파일 아래에 같은 패키지명으로 생성되어있따. 
> 참고: Q타입은 컴파일 시점에 자동 생성되므로 버전관리(GIT)에 포함하지 않는 것이 좋다. 앞서 설정에서
생성 위치를 gradle build 폴더 아래 생성되도록 했기 때문에 이 부분도 자연스럽게 해결된다. (대부분
gradle build 폴더를 git에 포함하지 않는다.)

*  참고: 스프링 부트에 아무런 설정도 하지 않으면 h2 DB를 메모리 모드로 JVM안에서 실행한다

# 라이브러리 살펴보기 

* gradle 의존관계 보기 : ./gradlew dependencies --configuration compileClasspath

### Querydsl 라이브러리 살펴보기
  * querydsl-apt: Querydsl 관련 코드 생성 기능 제공
  * querydsl-jpa: querydsl 라이브러리


# H2
H2 데이터베이스 설치
* 개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공
* https://www.h2database.com

다운로드 및 설치
* h2 데이터베이스 버전은 스프링 부트 버전에 맞춘다.
* 권한 주기: chmod 755 h2.sh

데이터베이스 파일 생성 방법
* jdbc:h2:~/querydsl (최소 한번)
  * 연결 성공하면 ~ 경로에 query.mv.db 파일이 생성된다 
* ~/querydsl.mv.db 파일 생성 확인
* 이후 부터는 jdbc:h2:tcp://localhost/~/querydsl 이렇게 접속
  * 인텔리제이로 어플리케이션을 실행하고, 따로 접속하면 에러가 나기 때문에 tcp 모드로 접속 
> 참고: H2 데이터베이스의 MVCC 옵션은 H2 1.4.198 버전부터 제거되었습니다. 이후 부터는 옵션 없이
사용하면 됩니다.
> 주의: 가급적 안정화 버전을 사용하세요. 1.4.200 버전은 몇가지 오류가 있습니다.
> 현재 안정화 버전은 1.4.199(2019-03-13) 입니다.
> 다운로드 링크: https://www.h2database.com/html/download.html


# 스프링부트 JPA,DB 설정 
```yaml

spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/querydsl
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
    # show_sql: true
      format_sql: true

logging.level:
  org.hibernate.SQL: debug
# org.hibernate.type: trace trace로 설정시 쿼리의 파라미터의 ? 가 보인다 
```
참고: 모든 로그 출력은 가급적 로거를 통해 남겨야 한다.
> show_sql : 옵션은 System.out 에 하이버네이트 실행 SQL을 남긴다.  
 

> org.hibernate.SQL : 옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.


##  쿼리 파라미터 로그 남기기
* 로그에 다음을 추가하기 org.hibernate.type : SQL 실행 파라미터를 로그로 남긴다.
* 외부 라이브러리 사용
  * https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
* 스프링 부트를 사용하면 이 라이브러리만 추가하면 된다
  * implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'

> 참고: 쿼리 파라미터를 로그로 남기는 외부 라이브러리는 시스템 자원을 사용하므로, 개발 단계에서는
편하게 사용해도 된다. 하지만 운영시스템에 적용하려면 꼭 성능테스트를 하고 사용하는 것이 좋다.

# 예제 도메인 모델 
* ![](img/95a6bdfc.png)

* 롬복 설명
* @Setter: 실무에서 가급적 Setter는 사용하지 않기
* @NoArgsConstructor AccessLevel.PROTECTED: 기본 생성자 막고 싶은데, JPA 스팩상
PROTECTED로 열어두어야 함
* @ToString은 가급적 내부 필드만(연관관계 없는 필드만)
* changeTeam() 으로 양방향 연관관계 한번에 처리(연관관계 편의 메소드)
