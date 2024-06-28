package com.unsolved.hguapis.user;

import com.unsolved.hguapis.usersolved.UserInfoProblemSolved;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class UserInfo {
    @Id
    private String username;

    @Column
    private int solvedCount;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column
    private int tier;

    @Column
    private int subClass;

    @Column
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "userInfo")
    Set<UserInfoProblemSolved> problemSolved;
}
