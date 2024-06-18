package com.unsolved.hguapis.problem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
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

    @OneToMany(mappedBy = "problem", cascade = CascadeType.REMOVE)
    private List<Tag> tags;
}
