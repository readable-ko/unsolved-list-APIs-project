package com.unsolved.hguapis.userinfo;

import com.unsolved.hguapis.problem.LevelType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserInfoDto {
    private String username;

    private int tier;

    private int solvedCount;

    private int subClass;

    private LocalDateTime modifyDate;

    public String getTier() {
        return LevelType.findLevel(tier);
    }

    public String getModifyDate() {
        return modifyDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
