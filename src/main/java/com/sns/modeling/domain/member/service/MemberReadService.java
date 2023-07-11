package com.sns.modeling.domain.member.service;

import com.sns.modeling.domain.member.dto.MemberDto;
import com.sns.modeling.domain.member.dto.MemberNicknameHistoryDto;
import com.sns.modeling.domain.member.entity.Member;
import com.sns.modeling.domain.member.entity.MemberNicknameHistory;
import com.sns.modeling.domain.member.repository.MemberNicknameHistoryRepository;
import com.sns.modeling.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberReadService {

  final private MemberRepository memberRepository;
  final private MemberNicknameHistoryRepository memberNicknameHistoryRepository;

  public MemberDto getMember(Long id) {
    var member = this.memberRepository.findById(id).orElseThrow();
    return this.toDto(member);
  }

  public List<MemberDto> getMembers(List<Long> ids){
    var members = this.memberRepository.findAllByIdIn(ids);
    return members.stream().map(this::toDto).toList();
  }

  public List<MemberNicknameHistoryDto> getNicknameHistories(Long memberId) {
    return this.memberNicknameHistoryRepository.finaAllbyMemberId(memberId).stream()
        .map(this::toDto).toList();
  }

  public MemberDto toDto(Member member) {
    return new MemberDto(member.getId(), member.getEmail(), member.getNickname(),
        member.getBirthDay());
  }

  private MemberNicknameHistoryDto toDto(MemberNicknameHistory history) {
    return new MemberNicknameHistoryDto(
        history.getId(),
        history.getMemberId(),
        history.getNickname(),
        history.getCreatedAt()
    );
  }
}
