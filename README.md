# SNS 서비스

# API

* 팔로우 정보
  * POST
    * /follow/{fromId}/{toId} : 팔로우 등록
      * 요구 사항
        * 이메일, 닉네임, 생년월일을 입력받아 저장한다.
        * 닉네임은 10자를 초과할 수 없다.
        * 회원은 닉네임을 변경 할수 있다.
          * 회원의 닉네임 변경 이력을 조회 할수 있어야 한다.
  * GET
    * /follow/member/{fromId} : 팔로워 조회
* 회원정보
  * POST
    * /member : 회원정보 등록
    * /member/{id}/name : 회원이름 변경
  * GET
    * /members/{id} : 회원정보 단건 조회
    * /members/{id}/name-histories : 회원이름 변경내역 조회

# 내가 쓴글 캘린더 
- 작성일자와 일자별 회원의 작성한 게시물 갯수를 반환한다.

# 공부 내용

## 1. Index 

### 0. 인덱스를 다룰 때 주의해야 할점

1. Cardinality가 높은 컬럼에 사용

* 중복이 적으면 카디널리티가 높다.
  * ex) 마트 품목(소고기/돼지고기/닭고기/콜라/사이다/햇반/어묵 등등....)
* 중복이 많으면 카디널리티가 낮다.
  * ex) 성별(남/여)1. 인덱스 필드 가공

#### 잘못된 예제

```
// age는 int 타입
SELECT *
FROM Member
WHERE age * 10 = 1 // 1. BTree 형태 이므로 가공 하면 Index 비교가 되지 않는다
WHERE age = '1' // 2. age는 int 타입이므로 Index 비교가 되지 않는다
```

### 1. 복합 인덱스

예제 1. 단일

| 과일 | PK |
| --- | --- |
| Apple | 1 |
| Banana | 4 |
| Banana | 5 |
| Butter | 3 |

예제 2. 복합

| 과일 | 원산지 | PK |
| --- | --- | --- |
| Apple | USA | 1 |
| Banana | CHINA | 4 |
| Banana | KOREA | 5 |
| Butter | KOREA | 3 |


> 예제 2번과 같이 복합 Index일 경우 "첫번째"로 "과일"이 정렬이 되고 이후 "원산지"가 정렬이 된다.  
> 따라서 WHERE 에 "원산지"만 비교 하면 Index를 비교 하지 않고  
> "과일"만 비교 하면 "Index를 비교" 한다.

#### 중요

> 따라서 복합 인덱스 일경우 선두 컬럼이 어떤거냐가 매우 중요하다!!

### 2. 하나의 쿼리에는 하나의 인덱스만

1.  하나의 쿼리에는 하나의 인덱스만 탄다.  
* 여러 인덱스 테이블을 동시에 탐색하지 않는다.
  * Index merge hint를 사용하면 가능
  * WHERE, ORDER BY, GROUP BY 혼합해서 사용할때는 Index를 잘 고려해야한다.
    * WHERE절에는 Index를 탔지만 ORDER BY, GROUP BY에는 Index를 타지 않으면 읽어온 데이터를 모두 다시 설정해야 한다.
2. 의도대로 인덱스가 동작하지 않을 수 있음. EXPLAIN으로 확인
3. INDEX도 비용이다. 쓰기를 희생하고 조회를 얻는 것
4. 꼭 인덱스로만 해결할 수 있는 문제인가?를 파악 해야 한다.

### 커버링 인덱스

> 검색 조건이 인덱스에 부합하다면, 테이블에 바로 접근 하는 것 보다 인덱스를 통해 접근하는 것이 매우 빠르다.(절대적이진 않다.)
> 그렇다면 테이블에 접근하지 않고 인덱스로만 데이터 응답을 내려줄 순 없을까? -> 커버링 인덱스
> (인덱스로만 커버 하겠다)

#### 예

* Index Table

| 나이 | id |
| --- | --- |
| 19 | 3 |
| 27 | 2 |
| 32 | 1 |
| 45 | 4 |

* 회원 Table

| id | 이름 | 성별 | 나이 | 직업 |
| --- | --- | --- | --- | --- |
| 1 | 홍길동 | 남 | 32 | 경찰 |
| 2 | 김천사 | 여 | 27 | 목사 |
| 3 | 이순신 | 남 | 19 | 어부 |
| 4 | 김철수 | 남 | 45 | 개발자 |

##### 1. 아래 쿼리를 실행하게 된다면..?

```
    SELECT 나이
    FROM 회원
    WHERE 나이 < 30
```

1.  Index Table에서 나이가 19, 27을 찾게 된다.
2.  이후 회원 Table 에서 나머지 정보를 찾게 된다.

하지만 원하는 정보는 '나이' 뿐이므로 굳이 회원 Table까지 갈 필요가 없다.

##### 2. 그렇다면 아래 쿼리를 실행하게 된다면..?

```
    SELECT 나이, id
    FROM 회원
    WHERE 나이 < 30
```

> id도 1. 번 쿼리 처럼 클러스터 인덱스로 존재 하기 때문에 커버링 인덱스 와 같이 실행이된다.

##### 따라서

> Mysql에서는 PK가 클러스터 인덱스이기 때문에 커버링 인덱스에 유리 하다

#### 그렇다면 커버링 인덱스로 페이지네이션 최적화를 어떻게 할 수 있을까?

##### 예) 나이가 30이하인 회원의 이름을 2개만 조회 한다면..?

```
    // ORDER BY, OFFSET, GROUP BY, LIMIT 절로 인한 불필요한 데이터블록 접근을 커버링 인덱스를 통해 최소화
    WITH 커버링 AS (
        SELECT id
        FROM 회원
        WHERE 나이 < 30
        LIMIT 2
    )

    SELECT 이름
    FROM 회원
    INNER JOIN 커버링 
        ON 회원.id = 커버링.id
```

##### 따라서 커버링 인덱스란?

> 불필요한 데이터 엑세스 접근을 커버링 인덱스로 범위를 줄여 놓고 찾는 데이터만 데이터 엑세스를 하도록 하는 기법

## 2. Pagination 방식 

### 1. OFFSET 방식
* LIMIT, OFFSET 을 사용하여 몇번째 부터 몇개씩 읽어 온다. 
* 장점
  * 간편하고 직관적인 구현이 가능하다.
  * 특정 페이지로 쉽게 이동할 수 있다.
* 단점
  * 대량의 데이터에서 성능이 저하될 수 있다. 페이지를 건너뛸 때마다 모든 이전 데이터를 가져와야 하므로 비효율적이다.
  * 데이터베이스에 새로운 항목이 추가되거나 삭제될 때 문제가 발생할 수 있다.
  
### 2. CURSOR 방식
* ID 기준으로 데이터를 뽑아온다.
* 장점
  * 대량의 데이터에서 효과적으로 작동한다. 페이지의 일부만 가져오므로 성능이 향상된다.
  * 데이터베이스에서 변경이 발생해도 영향을 받지 않는다. 
* 단점:
  * 특정 페이지로 직접 이동하기 어렵다.
  * 이전 페이지로 돌아가거나 특정 페이지로 이동하려면 추가적인 로직이 필요하다.

## 3. TimeLine

> Follow 같은 구조일 경우 Follow 된 User의 Post 까지 가져오는 2가지 방식을 설명

### 1. Pull Model(Fan Out On Read)
* 시간 복잡도
  * log(Follow 전체 레코드) + 해당회원의 Following * log(Post 전체 레코드)
* 단점 
  * 사용자가 매번 홈에 접속할때마다 부하가 발생한다.(사용자 Follow를 매번 OR 조건으로 검색해야 한다.)
* 사용 하는 업체 : FaceBook
  * 기술적 한계 로 인한 정책 
    * Facebook에서 최대 5000명의 친구를 보유할수 있습니다. 5000명 이상의 사람들과 연결해야 하는 경우 개인 계정을 Facebook 페이지로 변경하세요. 다른 친구 요청을 보내려면 먼저 누군가와 친구 관계를 끊어야 합니다.


### 2. Push Model (Fan Out On Write)
* 핵심 개념
  * 게시물 작성시, 해당 회원을 팔로우 하는 회원들에게 데이터를 배달한다.
  * 타임라인 테이블을 추가 하여 MemberId, PostId 컬럼으로 만든다. (MemberId로 만 검색 하면된다.)
* 사용 업체 : Twitter
  * 기술적 한계 로 인한 정책
    * 일일 한도 외에도 특정한 개수의 계정을 팔로잉할 때 적용이 되는 팔로우율이 있습니다. 모든 트위터 계쩡이 최대 5000개의 계정을 팔로우 할 수 있습니다. 이 한도에 도달 했다면 내 계정을 팔로우 하는 사람들이 늘어 날 떄가지 기다려야 추가로 계정을 팔로우 할수 있습니다. 이숫자는 계정마다 다르며 팔로워와 팔로잉의 고유한 비율을 기반으로 자동 계산됩니다.

### Pull/Push Model 간의 차이점

> 홈에 "접속"할때마다 생기는 부하(Pull Model)를 글을 "작성"할때 부하(Push Model)로 치환한다.

### 질문

* 질문.1 Push Model VS Pull Model 중 어떤 것이 정합성을 보장하기 쉬울까?
  *  Pull Model 은 원본 데이터를 직접 참조 하므로, 정합성 보장에 유리하지만 Follow 가 많은 회원일수록 처리 속도가 느리다.
  *  Push Model에서는 게시물 작성과 타임라인 배달의 적합성 보장에 대한 고민이 필요한다.
* 질문.2 모든 회원의 타임라인에 배달되기 전까지 게시물 작성의 트랜잭션을 유지 하는 것이 맞을까?
  * CAP 이론을 참고 하면 좋다.
  * Push Model은 Pull Model에 비해 시스템 복잡도가 높다. 하지만 그만큼 비즈니스, 기술 측면에서 유연성을 확보 시켜 준다.
  * 결국 은총알은 없다. 상황, 자원, 정책 등 여러가지를 고려해 트레이드 오프 해야 한다.

#### CAP 이론

> 일관성(Consistency), 가용성(Availability), 분할 내구성(Partition tolerance)의 세 가지 특성을 설명하는 분산 시스템의 이론입니다.<br> 
> 이 이론은 분산 시스템이 어떻게 동작해야 하는지에 대한 가이드라인을 제공합니다.

* 일관성(Consistency)
  * 모든 노드가 동일한 순간에 동일한 데이터를 볼 수 있어야 합니다. 
  * 어떤 노드에서 데이터가 갱신되면, 이 변경 사항은 다른 모든 노드에 즉시 반영되어야 합니다.
* 가용성(Availability)
  * 모든 요청은 성공 또는 실패로 완료되어야 하며, 응답이 없는 상태는 없어야 합니다. 
  * 어떤 노드가 실패하더라도 시스템은 계속해서 작동할 수 있어야 합니다.
* 분할 내구성(Partition Tolerance)
  * 네트워크의 일시적인 분할(네트워크 장애)이 발생하더라도 시스템은 계속해서 동작할 수 있어야 합니다. 
  * 분할이 발생하면 노드 간 통신이 불가능해질 수 있지만, 시스템은 여전히 일관성과 가용성을 유지해야 합니다.

> CAP 이론은 세 가지 특성 중에서 두 가지만을 선택할 수 있다는 개념으로, 현실적인 분산 시스템에서는 이 세 가지 특성을 완벽하게 동시에 만족시키기 어렵습니다. <br>
> 따라서 시스템 설계 시에 어떤 특성을 중요시하느냐에 따라 CAP 이론을 기반으로 선택을 고려해야 합니다.
