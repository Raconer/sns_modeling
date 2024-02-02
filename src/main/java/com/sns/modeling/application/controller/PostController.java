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

    // Pull Model
    @PostMapping
    public Long create(PostCommand command) {
        return this.postWriteService.create(command);
    }

    // Push Model
    @PostMapping("/timeline")
    public Long createWithTimeline(PostCommand command) {
        return this.createPostUseCase.execute(command);
    }

    // READ
    @GetMapping("/daily-post-count")
    public List<DailyPostCount> getDailyPostCounts(@ModelAttribute DailyPostCountRequest request) {
        return this.postReadService.getDailyPostCount(request);
    }

    // Pagination OFFSET 방식
    @GetMapping("/member/{memberId}")
    public Page<Post> getPost(
            @PathVariable("memberId") Long memberId,
            Pageable pageable
    ) {
        return this.postReadService.getPosts(memberId, pageable);
    }

    // Pagination Cursor 방식
    @GetMapping("/member/{memberId}/by-cursor")
    public PageCursor<Post> getPostByCursor(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.execute(memberId, cursorRequest);
    }

    // TimeLine

    // Pull Model
    @GetMapping("/member/{memberId}/tiemline")
    public PageCursor<Post> getTimeLine(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.execute(memberId, cursorRequest);
    }

    // PushModel
    @GetMapping("/member/{memberId}/tiemline/v2")
    public PageCursor<Post> getTimeLineV2(
            @PathVariable("memberId") Long memberId,
            CursorRequest cursorRequest
    ) {
        return this.getTimelinePostUseCase.executeByTimeline(memberId, cursorRequest);
    }
}
