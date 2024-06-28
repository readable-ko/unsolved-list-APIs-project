package com.unsolved.hguapis.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUsername(String username);

    @Query("SELECT"
            + " new com.unsolved.hguapis.user.UserInfoDto("
            + "u.username,"
            + "u.solvedCount,"
            + "u.comment,"
            + " u.tier,"
            + " u.subClass,"
            + " u.modifyDate)"
            + "FROM UserInfo u")
    List<UserInfoDto> findAllUserInfoDto(Sort solvedCount);
}
