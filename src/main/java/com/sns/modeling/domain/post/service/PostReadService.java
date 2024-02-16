package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.member.dto.PostDto;
import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.repository.PostLikeRepository;
import com.sns.modeling.domain.post.repository.PostRepository;

import java.util.List;

import com.sns.modeling.util.CursorRequest;
import com.sns.modeling.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReadService {
    final private PostRepository postRepository;
    final private PostLikeRepository postLikeRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        return this.postRepository.groupByCreatedDate(request);
    }

    public Post getPost(Long postId) {
        return this.postRepository.findById(postId, false)
                .orElseThrow();
    }

    public Page<PostDto> getPosts(Long memberId, Pageable pageable) {
        return this.postRepository.findAllByMemberId(memberId, pageable).map(this::toDto);
    }

    private PostDto toDto(Post post) {

        return new PostDto(post.getId(),
                           post.getContents(),
                           post.getCreatedAt(),
                           this.postLikeRepository.count(post.getId()));
    }

    public PageCursor<Post> getPosts(Long memberId, CursorRequest cursorRequest) {
        var posts = this.findAllBy(memberId, cursorRequest);
        var nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public PageCursor<Post> getPosts(List<Long> memberIds, CursorRequest cursorRequest) {
        var posts = this.findAllBy(memberIds, cursorRequest);
        var nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public List<Post> getPosts(List<Long> ids) {
        return this.postRepository.findAllByInId(ids);
    }

    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.haKey()) {
            return this.postRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(cursorRequest.key(),
                                                                                      memberId,
                                                                                      cursorRequest.size());
        }
        return this.postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
    }

    private List<Post> findAllBy(List<Long> memberId, CursorRequest cursorRequest) {
        if (cursorRequest.haKey()) {
            return this.postRepository.findAllByLessThanIdAndInMemberIdAndOrderByIdDesc(cursorRequest.key(),
                                                                                        memberId,
                                                                                        cursorRequest.size());
        }
        return this.postRepository.findAllByInMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
    }

    private static long getNextKey(List<Post> posts) {
        return posts.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
    }


}
