package com.unsolved.hguapis.siteUser;

import com.unsolved.hguapis.exception.DataNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser siteUser = SiteUser.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build();

        siteUserRepository.save(siteUser);
        return siteUser;
    }

    public SiteUser getSiteUser(String username) {
        Optional<SiteUser> siteUser = this.siteUserRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteUser not found");
        }
    }
}
