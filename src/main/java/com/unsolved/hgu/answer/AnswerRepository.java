package com.unsolved.hgu.answer;

import com.unsolved.hgu.user.SiteUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findAllByAuthor(SiteUser author);
}
