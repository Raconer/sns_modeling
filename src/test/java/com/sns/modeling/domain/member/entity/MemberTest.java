package com.sns.modeling.domain.member.entity;

import com.sns.modeling.util.MemberFixtureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

  /*Object Mother 패턴
   * 테스트를 할때 사용하는 패턴
   * 테스트 객체를 만드는 함수
   */

  @DisplayName("회원은 닉네임을 변경할 수 있다.")
  @Test
  public void testChangeName() {
    var member = MemberFixtureFactory.create();
    var expected = "pnu";

    member.changeNickname(expected);

    Assertions.assertEquals(expected, member.getNickname());
  }

  @DisplayName("회원의 닉네임은 10자를 초과 할수 없다.")
  @Test
  public void testNicknameMaxLength() {
    var member = MemberFixtureFactory.create();
    var overMaxLengthName = "pnupnupnupnu";

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> member.changeNickname(overMaxLengthName));
  }

}