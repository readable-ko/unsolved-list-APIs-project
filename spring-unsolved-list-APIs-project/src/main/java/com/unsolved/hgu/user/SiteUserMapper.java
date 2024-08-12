package com.unsolved.hgu.user;

import com.unsolved.hgu.problem.Problem;
import com.unsolved.hgu.problem.ProblemMapper;
import org.springframework.data.domain.Page;

public class SiteUserMapper {
    public static SiteUserDto toDto(SiteUser siteUser, Page<Problem> problems) {
        return new SiteUserDto(siteUser.getId(),
                siteUser.getUsername(),
                siteUser.getPassword(),
                siteUser.getEmail(),
                siteUser.getRole(),
                siteUser.getProvider(),
                siteUser.getProviderId(),
                problems.map(ProblemMapper::toDto)
        );
    }
}
