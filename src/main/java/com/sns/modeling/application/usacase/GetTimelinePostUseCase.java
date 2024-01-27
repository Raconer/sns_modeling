package com.sns.modeling.application.usacase;

import com.sns.modeling.domain.follow.entity.Follow;
import com.sns.modeling.domain.follow.service.FollowReadService;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.repository.PostRepository;
import com.sns.modeling.domain.post.service.PostReadService;
import com.sns.modeling.util.CursorRequest;
import com.sns.modeling.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTimelinePostUseCase {
    final private FollowReadService followReadService;
    final private PostReadService postReadService;
    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        /*
         * 1. memberId -> follow 조회
         * 2. 1번 결과로 게시물 조회
         */
        var following = this.followReadService.getFollowings(memberId);
        var followingMemberIds = following.stream().map(Follow::getToMemberId).toList();

        return this.postReadService.getPosts(followingMemberIds, cursorRequest);
    }
}
