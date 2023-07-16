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
