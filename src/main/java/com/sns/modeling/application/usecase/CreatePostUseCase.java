package com.sns.modeling.application.usecase;

import com.sns.modeling.domain.follow.entity.Follow;
import com.sns.modeling.domain.follow.service.FollowReadService;
import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.service.PostWriteService;
import com.sns.modeling.domain.post.service.TimelineWriteServce;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostUseCase {
    final private PostWriteService postWriteService;
    final private FollowReadService followReadService;
    final private TimelineWriteServce timelineWriteServce;

    // 하지만 트랜잭션을 설정하기 전에 고민을 해야 한다.
    // Follow가 몇 만명이라면 트랜잭션이 길어진다.
    // @Transactional
    public Long execute(PostCommand postCommand) {
        var postId = this.postWriteService.create(postCommand); // SQL 시작 1
        var followMemberIds = this.followReadService.getFollowers(postCommand.memberId())
                .stream()
                .map(Follow::getFromMemberId)
                .toList();
        this.timelineWriteServce.deliveryToTimeline(postId, followMemberIds); // SQL 시작 2

        return postId;
    }
}
