package com.unsolved.hgu.usersolved;

import com.unsolved.hgu.problem.Problem;
import com.unsolved.hgu.userinfo.UserInfo;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_info_problem_solved")
public class UserInfoProblemSolved {
    @EmbeddedId
    private UserInfoProblemSolvedId id;

    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemSolvedId")
    @JoinColumn(name = "problem_solved_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userInfoUsername")
    @JoinColumn(name = "user_info_username")
    private UserInfo userInfo;
}
