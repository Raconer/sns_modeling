package com.sns.modeling.domain.post.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record DailyPostCountRequest(
    Long memberId,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate firstDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate lastDate
) { }
