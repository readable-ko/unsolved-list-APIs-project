package com.unsolved.hguapis.problem;

import com.unsolved.hguapis.usersolved.UserInfoProblemSolved;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Problem {
    @Id
    private int id;

    @Column
    private String title;

    @Column
    // level 1~5: bronze, 6~10: silver, 11~15: gold
    private int level;

    @OneToMany(mappedBy = "problem")
    private Set<UserInfoProblemSolved> userInfos;

    @ManyToMany()
    Set<Tag> tags;
}
