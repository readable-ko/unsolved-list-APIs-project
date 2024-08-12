package com.unsolved.hgu.user;

import com.unsolved.hgu.problem.ProblemDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SiteUserDto {
    private Long id;

    private String username;

    private String password;

    private String email;

    private UserRole role;

    private String provider;

    private String providerId;

    private Page<ProblemDto> problems;
}
