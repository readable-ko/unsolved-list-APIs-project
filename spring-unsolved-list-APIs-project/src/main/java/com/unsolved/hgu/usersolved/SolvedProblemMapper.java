package com.unsolved.hgu.usersolved;

import com.unsolved.hgu.problem.ProblemDto;

public class SolvedProblemMapper {
    public static ProblemDto solvedToProblem(UserInfoProblemSolvedDto userInfoProblemSolvedDto) {
        return new ProblemDto(
                userInfoProblemSolvedDto.getProblem().getId(),
                userInfoProblemSolvedDto.getProblem().getTitle(),
                userInfoProblemSolvedDto.getProblem().getLevel(),
                Math.toIntExact(userInfoProblemSolvedDto.getCount()),
                userInfoProblemSolvedDto.getProblem().getTags()
        );
    }
}
