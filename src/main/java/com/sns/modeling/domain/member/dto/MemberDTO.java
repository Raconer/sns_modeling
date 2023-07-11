package com.sns.modeling.domain.member.dto;

import java.time.LocalDate;

public record MemberDTO(
    Long id,
    String email,
    String nickname,
    LocalDate birthday
){ }
