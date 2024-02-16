package com.sns.modeling.application.usecase;

import com.sns.modeling.domain.follow.service.FollowReadService;
import com.sns.modeling.domain.member.dto.MemberDto;
import com.sns.modeling.domain.member.service.MemberReadService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFollowMemberUsaCase {

    final private MemberReadService memberReadService;
    final private FollowReadService followReadService;

    public List<MemberDto> execute(Long memberId) {
        var followings = this.followReadService.getFollowings(memberId);
        var followingMemberIds = followings.stream().map(it -> it.getToMemberId()).toList();
        return this.memberReadService.getMembers(followingMemberIds);
    }
}