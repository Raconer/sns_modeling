package com.sns.modeling.controller;

import com.sns.modeling.domain.member.dto.MemberDTO;
import com.sns.modeling.domain.member.dto.MemberNicknameHistoryDto;
import com.sns.modeling.domain.member.dto.RegisterMemberCommand;
import com.sns.modeling.domain.member.service.MemberReadService;
import com.sns.modeling.domain.member.service.MemberWriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menbers")
public class MemberController {

    final private MemberWriteService memberWriteService;
    final private MemberReadService memberReadService;

    @PostMapping
    public MemberDTO create(@RequestBody RegisterMemberCommand command){
        var member = this.memberWriteService.create(command);
        return this.memberReadService.toDto(member);
    }

    @GetMapping("/{id}")
    public MemberDTO getMember(@PathVariable Long id){
        return this.memberReadService.getMember(id);
    }

    @GetMapping("/{memberId}/nickname-histories")
    public List<MemberNicknameHistoryDto> getNicknameHistories(@PathVariable Long memberId){
        return this.memberReadService.getNicknameHistories(memberId);
    }

    @PutMapping("/{id}/name")
    public MemberDTO changeNickname(@PathVariable Long id, @RequestBody String nickname ){
        this.memberWriteService.changeNickname(id, nickname);
        return this.memberReadService.getMember(id);
    }

}
