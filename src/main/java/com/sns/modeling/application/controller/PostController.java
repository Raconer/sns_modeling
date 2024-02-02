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
     * 사용 업체 : FaceBook
     *  - 기술적 한계 : Facebook에서 최대 5000명의 친구를 보유할수 있습니다. 5000명 이상의 사람들과 연결해야 하는 경우 개인 계정을 Facebook 페이지로 변경하세요. 다른 친구 요청을 보내려면 먼저 누군가와 친구 관계를 끊어야 합니다.
     *
     * Push Model (Fan Out On Write)
     * 핵심 개념 : 게시물 작성시, 해당 회원을 팔로우 하는 회원들에게 데이터를 배달한다.
     * 타임라인 테이블을 추가 하여 MemberId, PostId 컬럼으로 만든다. (MemberId로 만 검색 하면된다.)
     * 사용 업체 : Twitter
     *  - 기술적 한계 : 일일 한도 외에도 특정한 개수의 계정을 팔로잉할 때 적용이 되는 팔로우율이 있습니다. 모든 트위터 계쩡이 최대 5000개의 계정을 팔로우 할 수 있습니다. 이 한도에 도달 했다면 내 계정을 팔로우 하는 사람들이 늘어 날 떄가지 기다려야 추가로 계정을 팔로우 할수 있습니다. 이숫자는 계정마다 다르며 팔로워와 팔로잉의 고유한 비율을 기반으로 자동 계산됩니다.
     *
     * Pull/Push Model 간의 차이점
     * 홈에 접속할때마다 생기는 부하를 글을 작성할때 부하로 치환한다.
     */

    /*
     * 질문.1 Push Model VS Pull Model 중 어떤 것이 정합성을 보장하기 쉬울까?
     *  - Pull Model 은 원본 데이터를 직접 참조 하므로, 정합성 보장에 유리하지만 Follow 가 많은 회원일수록 처리 속도가 느리다.
     *  - Push Model에서는 게시물 작성과 타임라인 배달의 적합성 보장에 대한 고민이 필요한다.
     * 질문.2 모든 회원의 타임라인에 배달되기 전까지 게시물 작성의 트랜잭션을 유지 하는 것이 맞을까?
     * - CAP 이론을 참고 하면 좋다.
     * - Push Model은 Pull Model에 비해 시스템 복잡도가 높다. 하지만 그만큼 비즈니스, 기술 측면에서 유연성을 확보 시켜 준다.
     * - 결국 은총알은 없다. 상황, 자원, 정책 등 여러가지를 고려해 트레이드 오프 해야 한다.
     * */
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
