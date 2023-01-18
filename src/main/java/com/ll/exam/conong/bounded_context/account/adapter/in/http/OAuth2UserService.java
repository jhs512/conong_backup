package com.ll.exam.conong.bounded_context.account.adapter.in.http;

import com.ll.exam.conong.base.security.User;
import com.ll.exam.conong.bounded_context.account.domain.service.AccountService;
import com.ll.exam.conong.bounded_context.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final AccountService accountService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthId = oAuth2User.getName();

        String oauthType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (!"KAKAO".equals(oauthType)) {
            throw new RuntimeException("카카오로그인만 가능합니다.");
        }

        String nickname = null;
        String email = null;
        String username = null;
        String profileImgUrl = null;

        switch (oauthType) {
            case "KAKAO" -> {
                Map attributesProperties = (Map) attributes.get("properties");
                nickname = (String) attributesProperties.get("nickname");
                profileImgUrl = (String) attributesProperties.get("profile_image");
                email = "%s@kakao.com".formatted(oauthId);
                username = "KAKAO_%s".formatted(oauthId);
            }
        }

        Member member = accountService.whenSocialLogin(oauthType, username, email, nickname, profileImgUrl);

        return User.from(member);
    }
}
