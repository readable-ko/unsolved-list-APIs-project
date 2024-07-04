package com.unsolved.hguapis.problem;

import lombok.Getter;

@Getter
public enum LevelType {
    NONE("없음", 0),
    BRONZE("브론즈", 5),
    SILVER("실버", 10),
    GOLD("골드", 15),
    PLATINUM("플래티넘", 20),
    DIAMOND("다이아", 25),
    RUBY("루비", 30),
    FAVORITE("많이", 40);

    private final String title;
    private final int level;

    LevelType(String title, int level) {
        this.title = title;
        this.level = level;
    }

    public static String findLevel(int level) {
        for (LevelType levelType : LevelType.values()) {
            if (levelType.level >= level) {
                int levelNum = (level - 1) % 5;
                return levelType.title + LevelNumberType.findLevel(levelNum);
            }
        }
        return LevelType.NONE.title;
    }

    public boolean isNotNone() {
        return this != NONE;
    }
}
