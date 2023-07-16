package com.sns.modeling.domain.post.dto;

public record PostCommand(
    Long memberId,
    String contents
){}
