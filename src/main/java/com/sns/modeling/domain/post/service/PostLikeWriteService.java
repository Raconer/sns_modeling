package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.member.dto.MemberDto;
import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.entity.PostLike;
import com.sns.modeling.domain.post.repository.PostLikeRepository;
import com.sns.modeling.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeWriteService {

    final private PostLikeRepository postLikeRepository;

    public Long create(Post post, MemberDto memberDto) {
        var postLike = PostLike.builder()
                .postId(post.getId())
                .memberId(memberDto.id())
                .build();

        return this.postLikeRepository.save(postLike).getPostId();
    }

}