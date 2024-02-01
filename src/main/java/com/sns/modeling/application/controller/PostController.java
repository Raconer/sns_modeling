package com.sns.modeling.application.controller;

import com.sns.modeling.application.usacase.CreatePostUseCase;
import com.sns.modeling.application.usacase.GetTimelinePostUseCase;
import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.service.PostReadService;
import com.sns.modeling.domain.post.service.PostWriteService;

import java.util.List;

import com.sns.modeling.util.CursorRequest;
import com.sns.modeling.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    final private PostWriteService postWriteService;
    final private PostReadService postReadService;
    final private GetTimelinePostUseCase getTimelinePostUseCase;
    final private CreatePostUseCase createPostUseCase;
    // CREATE
    @PostMapping
    public Long create(PostCommand command) {
        return this.postWriteService.create(command);
    }

    @PostMapping("/timeline")
    public Long createWithTimeline(PostCommand command) {
        return this.createPostUseCase.execute(command);
    }

    // READ
    @GetMapping("/daily-post-count")
    public List<DailyPostCount> getDailyPostCounts(@ModelAttribute DailyPostCountRequest request) {
        return this.postReadService.getDailyPostCount(request);
    }


    // 오프셋 방식(LIMIT, OFFSET 을 사용하여 몇번째 부터 몇개씩 읽어 온다.)
    // 장점:
    // * 간편하고 직관적인 구현이 가능하다.
    // * 특정 페이지로 쉽게 이동할 수 있다.
    // 단점:
    // * 대량의 데이터에서 성능이 저하될 수 있다. 페이지를 건너뛸 때마다 모든 이전 데이터를 가져와야 하므로 비효율적이다.
    // * 데이터베이스에 새로운 항목이 추가되거나 삭제될 때 문제가 발생할 수 있다.
    @GetMapping("/member/{memberId}")
    public Page<Post> getPost(
            @PathVariable("memberId") Long memberId,
            Pageable pageable
    ) {
        return this.postReadService.getPosts(memberId, pageable);
    }

    // 커서 방식 (ID 기준으로 데이터를 뽑아온다.)
    // 장점:
    // * 대량의 데이터에서 효과적으로 작동한다. 페이지의 일부만 가져오므로 성능이 향상된다.
    // * 데이터베이스에서 변경이 발생해도 영향을 받지 않는다.
    // 단점:
    // * 특정 페이지로 직접 이동하기 어렵다.
    // * 이전 페이지로 돌아가거나 특정 페이지로 이동하려면 추가적인 로직이 필요하다.
    @GetMapping("/member/{memberId}/by-cursor")
    public PageCursor<Post> getPostByCursor(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.execute(memberId, cursorRequest);
    }

    /*
     * Pull Model(Fan Out On Read)
     * 시간 복잡도
     * log(Follow 전체 레코드) + 해당회원의 Following * log(Post 전체 레코드)
     * 단점 : 사용자가 매번 홈에 접속할때마다 부하가 발생한다.(사용자 Follow를 매번 OR 조건으로 검색해야 한다.)
     *
     * Push Model (Fan Out On Write)
     * 핵심 개념 : 게시물 작성시, 해당 회원을 팔로우 하는 회원들에게 데이터를 배달한다.
     * 타임라인 테이블을 추가 하여 MemberId, PostId 컬럼으로 만든다. (MemberId로 만 검색 하면된다.)
     *
     * Pull/Push Model 간의 차이점
     * 홈에 접속할때마다 생기는 부하를 글을 작성할때 부하로 치환한다.
     */
    @GetMapping("/member/{memberId}/tiemline")
    public PageCursor<Post> getTimeLine(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.execute(memberId, cursorRequest);
    }

    @GetMapping("/member/{memberId}/tiemline/v2")
    public PageCursor<Post> getTimeLineV2(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.executeByTimeline(memberId, cursorRequest);
    }
}
