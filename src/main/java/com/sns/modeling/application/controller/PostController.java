package com.sns.modeling.application.controller;

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
  @PostMapping
  public Long create(PostCommand command){
    return this.postWriteService.create(command);
  }
  @GetMapping("/daily-post-count")
  public List<DailyPostCount> getDailyPostCounts(@ModelAttribute DailyPostCountRequest request){
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
  ){
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
  ){
    return this.postReadService.getPosts(memberId, cursorRequest);
  }

}
