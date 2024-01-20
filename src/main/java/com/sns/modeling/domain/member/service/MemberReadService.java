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


    /**
     * 단건 조회
     *
     * @param id 사용자 ID
     * @return 사용자 정보 DTO
     */
    public MemberDto getMember(Long id) {
        var member = this.memberRepository.findById(id).orElseThrow();
        return this.toDto(member);
    }

    /**
     * 다건 조회
     *
     * @param ids 사용자 ID List
     * @return 사용자 정보 DTO List
     */
    public List<MemberDto> getMembers(List<Long> ids) {
        var members = this.memberRepository.findAllByIdIn(ids);
        return members.stream().map(this::toDto).toList();
    }
    /**
     * 닉네임 변경이력 조회
     *
     * @param memberId 사용자 ID
     * @return 변경이력 조회 List
     */
    public List<MemberNicknameHistoryDto> getNicknameHistories(Long memberId) {
        return this.memberNicknameHistoryRepository
                .finaAllbyMemberId(memberId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 데이터 맵핑
    public MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
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
