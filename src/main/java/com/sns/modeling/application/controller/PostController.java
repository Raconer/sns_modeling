package com.sns.modeling.application.controller;

import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.service.PostReadService;
import com.sns.modeling.domain.post.service.PostWriteService;
import java.util.List;
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
  @GetMapping("/member/{memberId}")
  public Page<Post> getPost(
          @PathVariable("memberId") Long memberId,
           Pageable pageable
  ){
    return this.postReadService.getPosts(memberId, pageable);
  }
}
