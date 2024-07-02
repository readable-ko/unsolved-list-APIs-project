package com.unsolved.hguapis.usersolved;

import com.unsolved.hguapis.problem.ProblemDto;

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
