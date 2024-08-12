package com.unsolved.hgu.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByEmailAndProvider(email, "site");
        return getUserDetails(_siteUser);
    }

    public UserDetails loadUserByEmailAndProvider(String email, String provider) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByEmailAndProvider(email, provider);
        return getUserDetails(_siteUser);
    }

    private UserDetails getUserDetails(Optional<SiteUser> _siteUser) {
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        SiteUser siteUser = _siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}
