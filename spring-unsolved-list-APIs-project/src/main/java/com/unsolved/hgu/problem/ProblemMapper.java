package com.unsolved.hgu.problem;

import java.util.Set;

public class ProblemMapper {
    public static ProblemDto toDto(Problem problem) {
        Set<Tag> tags = problem.getTags();
        return new ProblemDto(
                problem.getId(),
                problem.getTitle(),
                problem.getLevel(),
                0,
                tags
        );
    }
}
