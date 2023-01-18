package com.ll.exam.conong.bounded_context.member.domain.service;

import com.ll.exam.conong.bounded_context.member.adapter.out.jpa.MemberRepository;
import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> getMemberByUsername(String username) {
        return memberRepository.findMemberByUsername(username);
    }

    public Member join(String oauthType, String username, String email, String nickname, String profileImgUrl) {
        Member member = Member.builder()
                .oauthType(oauthType)
                .username(username)
                .password("")
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();

        memberRepository.save(member);

        return member;
    }
}
