package com.sns.modeling.application.usacase;

import com.sns.modeling.domain.follow.service.FollowWriteService;
import com.sns.modeling.domain.member.service.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateFollowMemberUseCase {

  final private MemberReadService memberReadService;
  final private FollowWriteService followWriteService;

  /*
      1. 입력받은 memberId로 조회
      2. FollowWriteService.create()
   */
  public void execute(Long fromMemberId, Long toMemberId){
    var fromMember = this.memberReadService.getMember(fromMemberId);
    var toMember = this.memberReadService.getMember(toMemberId);

    this.followWriteService.create(fromMember, toMember);
  }

}
