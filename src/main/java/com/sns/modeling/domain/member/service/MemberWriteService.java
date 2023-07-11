package com.sns.modeling.domain.member.service;

import com.sns.modeling.domain.member.dto.RegisterMemberCommand;
import com.sns.modeling.domain.member.entity.Member;
import com.sns.modeling.domain.member.entity.MemberNicknameHistory;
import com.sns.modeling.domain.member.repository.MemberNicknameHistoryRepository;
import com.sns.modeling.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberWriteService {

  final private MemberRepository memberRepository;
  final private MemberNicknameHistoryRepository memberNicknameHistoryRepository;

  public Member create(RegisterMemberCommand command) {
    Member member = Member.builder()
        .nickname(command.nickname())
        .email(command.email())
        .birthDay(command.birthDay())
        .build();

    var savedMember = this.memberRepository.save(member);
    this.saveMemberNicknameHistory(savedMember);
    return savedMember;
  }

  public void changeNickname(Long memberId, String nickname) {
    var member = memberRepository.findById(memberId).orElseThrow();
    member.changeNickname(nickname);
    this.memberRepository.save(member);

    saveMemberNicknameHistory(member);
  }

  private void saveMemberNicknameHistory(Member member) {
    var history = MemberNicknameHistory
        .builder()
        .memberId(member.getId())
        .nickname(member.getNickname())
        .build();

    this.memberNicknameHistoryRepository.save(history);
  }
}
