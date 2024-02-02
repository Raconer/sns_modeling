package com.sns.modeling.domain.follow.service;

import com.sns.modeling.domain.follow.entity.Follow;
import com.sns.modeling.domain.follow.repository.FollowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowReadService {

  final private FollowRepository followRepository;

  public List<Follow> getFollowings(Long memberId){
    return this.followRepository.findAllByFromMemberId(memberId);
  }
  public List<Follow> getFollowers(Long memberId){
    return this.followRepository.findAllByToMemberId(memberId);
  }

}
