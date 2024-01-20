package com.sns.modeling.domain.post.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Post {
    final private Long id;
    final private Long memberId;
    final private String contents;
    final private LocalDate createdDate;
    final private LocalDateTime createdAt;

    @Builder
    public Post(Long id,
                Long memberId,
                String contents,
                LocalDate createdDate,
                LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(contents);
        this.createdDate = createdDate == null ? LocalDate.now() : createdDate;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}