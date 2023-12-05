package com.miraclerun.petopia.controller;

import com.miraclerun.petopia.domain.Member;
import com.miraclerun.petopia.dto.MemberDto;
import com.miraclerun.petopia.request.CreateMemberRequest;
import com.miraclerun.petopia.request.GetMembersRequest;
import com.miraclerun.petopia.response.CreateMemberResponse;
import com.miraclerun.petopia.response.GetMembersResponse;
import com.miraclerun.petopia.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/member")
    public CreateMemberResponse join(@RequestBody CreateMemberRequest request) {
        Long memberId = memberService.createMember(request);

        return CreateMemberResponse.builder()
                .id(memberId)
                .build();
    }

    /**
     * 회원 단건 조회
     */
    @GetMapping("/member/{memberId}")
    public MemberDto member(@PathVariable Long memberId) {
        Member member = memberService.getMember(memberId);

        return new MemberDto(member);
    }

    /**
     * 회원 전체 조회
     */
    @GetMapping("/member")
    public GetMembersResponse members(@RequestParam int page) {
        GetMembersRequest request = GetMembersRequest.builder()
                .page(page)
                .build();
        List<Member> members = memberService.getMembers(request);
        int totalPages = memberService.getTotalPages(request);

        List<MemberDto> collect = members.stream()
                .map(MemberDto::new)
                .toList();

        return GetMembersResponse.builder()
                .totalPages(totalPages)
                .members(collect)
                .build();
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/member/{memberId}")
    public void delete(@PathVariable Long memberId) {

        memberService.deleteMember(memberId);
    }
}
