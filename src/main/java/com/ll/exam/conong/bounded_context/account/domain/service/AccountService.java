package com.ll.exam.conong.bounded_context.account.domain.service;

import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import com.ll.exam.conong.bounded_context.member.domain.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final MemberService memberService;

    public Member whenSocialLogin(String oauthType, String username, String email, String nickname, String profileImgUrl) {
        Optional<Member> memberByUsername = memberService.getMemberByUsername(username);

        if ( memberByUsername.isPresent() ) {
            return memberByUsername.get();
        }

        return joinWithSocialLogin(oauthType, username, email, nickname, profileImgUrl);
    }

    private Member joinWithSocialLogin(String oauthType, String username, String email, String nickname, String profileImgUrl) {
        return memberService.join(oauthType, username, email, nickname, profileImgUrl);
    }
}
