package com.sns.modeling.application.usecase;

import com.sns.modeling.domain.follow.entity.Follow;
import com.sns.modeling.domain.follow.service.FollowReadService;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.entity.Timeline;
import com.sns.modeling.domain.post.service.PostReadService;
import com.sns.modeling.domain.post.service.TimelineReadService;
import com.sns.modeling.util.CursorRequest;
import com.sns.modeling.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTimelinePostUseCase {
    final private FollowReadService followReadService;
    final private PostReadService postReadService;
    final private TimelineReadService timelineReadService;
    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        /*
         * 1. memberId -> follow 조회
         * 2. 1번 결과로 게시물 조회
         */
        var following = this.followReadService.getFollowings(memberId);
        var followingMemberIds = following.stream().map(Follow::getToMemberId).toList();

        return this.postReadService.getPosts(followingMemberIds, cursorRequest);
    }

    public PageCursor<Post> executeByTimeline(Long memberId, CursorRequest cursorRequest) {
        /*
         * 1. Timeline 조회
         * 2. 1번에 해당하는 게시물을 조회한다.
         */
        var pagedTimelines = this.timelineReadService.getTimelines(memberId, cursorRequest);
        var postIds = pagedTimelines.body().stream().map(Timeline::getPostId).toList();
        var posts = this.postReadService.getPosts(postIds);
        return new PageCursor<>(pagedTimelines.nextCursorRequest(), posts);
    }


}
