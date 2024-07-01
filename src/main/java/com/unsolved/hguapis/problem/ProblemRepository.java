package com.unsolved.hguapis.problem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    //    select * from problem
//    WHERE problem.id not in (SELECT DISTINCT problem_solved_id FROM user_info_problem_solved)
    @Query("SELECT p "
            + "FROM Problem p "
            + "WHERE p.id NOT IN "
            + "(SELECT DISTINCT up.id.problemSolvedId "
            + "FROM UserInfoProblemSolved up) ")
    Page<Problem> findUnsolvedProblems(Pageable pageable);
}
