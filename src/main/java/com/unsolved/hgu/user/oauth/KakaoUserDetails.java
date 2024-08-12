package com.unsolved.hgu.user.oauth;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KakaoUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return (String) ((Map<?, ?>) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) ((Map<?, ?>) attributes.get("kakao_account")).get("email");
    }
}
