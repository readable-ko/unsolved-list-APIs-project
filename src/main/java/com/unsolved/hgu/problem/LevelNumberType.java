package com.unsolved.hgu.problem;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum LevelNumberType {
    NONE("NONE", -1),
    ONE("1", 4),
    TWO("2", 3),
    THREE("3", 2),
    FOUR("4", 1),
    FIVE("5", 0);

    private final String level;
    private final int value;

    LevelNumberType(String s, int n) {
        this.level = s;
        this.value = n;
    }

    public static String findLevel(int givenValue) {
        return Arrays.stream(values())
                .filter(levelNumberType -> levelNumberType.value == givenValue)
                .findFirst()
                .orElse(NONE)
                .getLevel()
                ;
    }
}
