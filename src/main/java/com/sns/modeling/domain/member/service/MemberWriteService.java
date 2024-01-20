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

    /**
     * 닉네임 변경이력 조회
     *
     * @param command 사용자 ID
     * @return 변경이력 조회 List
     */
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

    /**
     * Nickname 변경
     *
     * @param memberId 사용자 ID
     * @param nickname 변경할 nickname
     */
    public void changeNickname(Long memberId, String nickname) {
        var member = memberRepository.findById(memberId).orElseThrow();
        member.changeNickname(nickname);

        // Change NickName
        this.memberRepository.save(member);
        // Create NickName History
        this.saveMemberNicknameHistory(member);
    }

   /**
    * Member NickName 변경
    * 
    * @param member 변경할 NickName 및 memberId
    *
    */
    private void saveMemberNicknameHistory(Member member) {
        var history = MemberNicknameHistory
                .builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();

        this.memberNicknameHistoryRepository.save(history);
    }
}
