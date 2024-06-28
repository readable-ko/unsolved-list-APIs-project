package com.unsolved.hguapis.user;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserInfoDto {
    private String username;

    private int solvedCount;

    private String comment;

    private int tier;

    private int subClass;

    private LocalDateTime modifyDate;
}
