package com.unsolved.hgu.answer;

import com.unsolved.hgu.question.Question;
import com.unsolved.hgu.user.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    @Column
    private LocalDateTime modifiedDate;

    @ManyToMany
    Set<SiteUser> voter;
}
