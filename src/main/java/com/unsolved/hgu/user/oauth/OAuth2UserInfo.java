package com.unsolved.hgu.user.oauth;

// 차후 구글 이외 카카오, 네이버 등 소셜 로그인을 위한 틀 마련
public interface OAuth2UserInfo {
    String getProvider();

    String getProviderId();

    String getName();

    String getEmail();
}
