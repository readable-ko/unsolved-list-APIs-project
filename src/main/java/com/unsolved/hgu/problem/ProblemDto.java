package com.unsolved.hgu.problem;

import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProblemDto {
    private int id;

    private String title;

    private int level;

    private int solvedCount;

    private Set<Tag> tags;

    public String getTagName() {
        return tags.stream().
                map(Tag::getName)
                .toList().toString();
    }

    public String getStringLevel() {
        return LevelType.findLevel(this.level);
    }
}
