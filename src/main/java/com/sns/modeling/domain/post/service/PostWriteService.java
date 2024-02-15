package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.post.dto.PostCommand;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    final private PostRepository postRepository;

    public Long create(PostCommand command) {
        var post = Post.builder()
                .memberId(command.memberId())
                .contents(command.contents())
                .build();
        return postRepository.save(post).getId();
    }

    /**
     *  트랜잭션도 열고, requiredLock(For Update) 과 Commit도 찍어야 하니
     *  성능적으로는 Optimistic Lock이 더 좋다(심지어 각각 다른 트랜잭션으로 동작 된다.)
     *  실제 실무에서 Optimistic Lock을 부담없이 사용한다.(JPA에서 @OptimisticLock 으로 쉽게 구현가능하다./ 그리고 두개의 Lock을 섞어서 사용 되기도 한다.)
     *  하지만 분산 환경으로 가게 되면 비관적락을 간단하게 사용할수 없다.(로직에서 실수가 일어 날수있다.)
     *  그래서 Optimistic Lock 추가적으로 적용해서 개발한다.
     */

    @Transactional
    public void likePost(Long postId){
        var post = this.postRepository.findById(postId, true).orElseThrow();
        post.incrementLikeCount();
        this.postRepository.save(post);
    }

    public void likePostByOptimisticLock(Long postId){
        var post = this.postRepository.findById(postId, false).orElseThrow();
        post.incrementLikeCount();
        this.postRepository.save(post);
    }
}