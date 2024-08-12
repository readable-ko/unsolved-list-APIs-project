package com.unsolved.hgu.user.oauth;

import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OAuthType {
    GOOGLE("google", GoogleUserDetails::new),
    KAKAO("kakao", KakaoUserDetails::new),
    NAVER("naver", NaverUserDetails::new);

    private final String provider;
    private final Function<Map<String, Object>, OAuth2UserInfo> userDetailsFunction;

    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        return userDetailsFunction.apply(attributes);
    }
}