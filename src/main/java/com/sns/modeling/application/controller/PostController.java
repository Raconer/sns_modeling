package com.sns.modeling.application.controller;

import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.service.PostReadService;
import com.sns.modeling.domain.post.service.PostWriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
