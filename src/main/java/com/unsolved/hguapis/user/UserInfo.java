package com.unsolved.hguapis.user;

import com.unsolved.hguapis.problem.Problem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column
    private String solvedCount;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column
    private int tier;

    @Column
    private String subClass;

    @Column
    private LocalDateTime modifyDate;

    @ManyToMany
    Set<Problem> problemSolved;
}
