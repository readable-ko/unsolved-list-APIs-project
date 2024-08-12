package com.unsolved.hgu.usersolved;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoProblemSolvedService {
    private final UserInfoProblemSolvedRepository userInfoProblemSolvedRepository;

    public Page<UserInfoProblemSolvedDto> getProblems(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.userInfoProblemSolvedRepository.findFavoriteProblems(keyword, pageable);
    }
}
