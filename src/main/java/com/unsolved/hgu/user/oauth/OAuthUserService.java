package com.unsolved.hgu.user.oauth;

import com.unsolved.hgu.exception.DataNotFoundException;
import com.unsolved.hgu.user.SiteUser;
import com.unsolved.hgu.user.UserRepository;
import com.unsolved.hgu.user.UserRole;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(oAuth2User, provider);

        Optional<SiteUser> findUser = userRepository.findByLoginId(provider, oAuth2UserInfo.getProviderId());

        if (findUser.isPresent()) {
            return new OAuth2UserDetails(findUser.get(), oAuth2User.getAttributes());
        }

        String username = oAuth2UserInfo.getName() + "_" + provider + "_" + oAuth2UserInfo.getEmail().split("@")[0];

        SiteUser siteUser = SiteUser.builder()
                .username(username)
                .email(oAuth2UserInfo.getEmail())
                .provider(provider)
                .providerId(oAuth2UserInfo.getProviderId())
                .role(UserRole.USER)
                .build();

        userRepository.save(siteUser);

        return new OAuth2UserDetails(siteUser, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User, String provider) {
        //Find provider
        for (OAuthType oAuthType : OAuthType.values()) {
            if (oAuthType.getProvider().equals(provider)) {
                return oAuthType.getUserInfo(oAuth2User.getAttributes());
            }
        }
        throw new DataNotFoundException("No user found with provider " + provider);
    }
}