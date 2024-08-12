package com.unsolved.hgu.userinfo;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoContributeDto {
    private String username;

    private int tier;

    private long solvedCount;

    private int subClass;

    private LocalDateTime modifyDate;
}
