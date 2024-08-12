package com.unsolved.hgu.usersolved;

import com.unsolved.hgu.problem.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoProblemSolvedDto {
    private int id;

    private Problem problem;

    private Long count;
}
