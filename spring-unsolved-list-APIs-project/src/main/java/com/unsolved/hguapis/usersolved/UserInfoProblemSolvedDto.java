package com.unsolved.hguapis.usersolved;

import com.unsolved.hguapis.problem.Problem;
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
