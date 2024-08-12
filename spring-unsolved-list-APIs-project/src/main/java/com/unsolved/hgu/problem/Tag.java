package com.unsolved.hgu.problem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Tag {
    @Id
    private String name;

    @Column(unique = true, nullable = false)
    private int id;

    @Column
    private String engName;
}
