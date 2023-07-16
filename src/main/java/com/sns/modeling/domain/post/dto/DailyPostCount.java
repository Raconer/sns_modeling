package com.sns.modeling.domain.post.dto;

import java.time.LocalDate;

public record DailyPostCount(
    Long memberId,
    LocalDate date,
    Long postCount
) {

}
