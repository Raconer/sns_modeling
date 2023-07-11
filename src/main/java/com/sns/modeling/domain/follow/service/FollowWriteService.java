package com.sns.modeling.domain.follow.service;

import com.sns.modeling.domain.follow.entity.Follow;
import com.sns.modeling.domain.follow.repository.FollowRepository;
import com.sns.modeling.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class FollowWriteService {

  final private FollowRepository followRepository;

  public void create(MemberDto fromMember, MemberDto toMember){
    Assert.isTrue(!fromMember.id().equals(toMember.id()) , "From, To 회원이 동일합니다.");

    var follow = Follow.builder()
        .fromMemberId(fromMember.id())
        .toMemberId(toMember.id())
        .build();
    this.followRepository.save(follow);
  }
}
