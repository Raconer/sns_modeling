package com.sns.modeling.application.usecase;

import com.sns.modeling.domain.member.service.MemberReadService;
import com.sns.modeling.domain.post.service.PostLikeWriteService;
import com.sns.modeling.domain.post.service.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostLikeUseCase {
    final private PostReadService postReadService;
    final private MemberReadService memberReadService;
    final private PostLikeWriteService postLikeWriteService;

    public void execute(Long postId, Long memberId){
        var post = this.postReadService.getPost(postId);
        var member = this.memberReadService.getMember(memberId);

        this.postLikeWriteService.create(post, member);

    }
}
