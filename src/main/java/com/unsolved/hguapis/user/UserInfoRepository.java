package com.unsolved.hguapis.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("SELECT"
            + " new com.unsolved.hguapis.user.UserInfoDto("
            + "u.username,"
            + "u.tier, "
            + "u.solvedCount, "
            + " u.subClass,"
            + " u.modifyDate) "
            + "FROM UserInfo u "
            + "WHERE u.username LIKE %:kw% ")
    Page<UserInfoDto> findAllUserInfo(@Param("kw") String kw, Pageable pageable);

    @Query("SELECT"
            + " new com.unsolved.hguapis.user.UserInfoContributeDto("
            + "u.username,"
            + "u.tier, "
            + "COUNT(*), "
            + " u.subClass,"
            + " u.modifyDate) FROM UserInfo u JOIN UserInfoProblemSolved uip ON uip.id.userInfoUsername = u.username "
            + "WHERE uip.id.problemSolvedId IN ( "
            + "SELECT up.id.problemSolvedId "
            + "FROM UserInfoProblemSolved up "
            + "GROUP BY up.id.problemSolvedId "
            + "HAVING COUNT(up.id.userInfoUsername) = 1 ) AND "
            + "u.username LIKE %:kw% "
            + "GROUP BY u.username, u.tier, u.subClass, u.modifyDate "
            + "ORDER BY COUNT(*) DESC ")
    Page<UserInfoContributeDto> findContributeUserInfo(@Param("kw") String kw, Pageable pageable);
}
