package com.unsolved.hgu.usersolved;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInfoProblemSolvedRepository extends JpaRepository<UserInfoProblemSolved, UserInfoProblemSolvedId> {
    @Query("SELECT "
            + "new com.unsolved.hgu.usersolved.UserInfoProblemSolvedDto(up.id.problemSolvedId, up.problem, COUNT(up.userInfo.username)) "
            + "FROM UserInfoProblemSolved up "
            + "LEFT JOIN Problem p ON p.id = up.id.problemSolvedId "
            + "GROUP BY up.id.problemSolvedId "
            + "ORDER BY COUNT(up.userInfo.username) DESC ")
    Page<UserInfoProblemSolvedDto> findFavoriteProblems(@Param("keyword") String keyword, Pageable pageable);
}
