package com.sns.modeling.application.controller;

import com.sns.modeling.application.usacase.CreateFollowMemberUseCase;
import com.sns.modeling.application.usacase.GetFollowMemberUsacase;
import com.sns.modeling.domain.member.dto.MemberDto;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
public class FollowController {

  final private CreateFollowMemberUseCase createFollowMemberUsecase;
  final private GetFollowMemberUsacase getFollowMemberUsacase;

  @PostMapping("/{fromId}/{toId}")
  public void create(@PathVariable Long fromId, @PathVariable Long toId){
    this.createFollowMemberUsecase.execute(fromId, toId);
  }

  @GetMapping("/members/{fromId}")
  public List<MemberDto> getMembers(@PathVariable Long fromId){
    return this.getFollowMemberUsacase.execute(fromId);
  }
}
