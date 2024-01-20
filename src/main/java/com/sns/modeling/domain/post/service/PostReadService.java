package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.repository.PostRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReadService {
    final private PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        return this.postRepository.groupByCreatedDate(request);
    }
}
