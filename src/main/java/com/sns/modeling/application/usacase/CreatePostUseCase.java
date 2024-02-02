package com.sns.modeling.application.usacase;

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

    public Long execute(PostCommand postCommand) {
        var postId = this.postWriteService.create(postCommand);
        var followMemberIds = this.followReadService.getFollowers(postCommand.memberId())
                .stream()
                .map(Follow::getFromMemberId)
                .toList();
        this.timelineWriteServce.deliveryToTimeline(postId, followMemberIds);

        return postId;
    }
}
