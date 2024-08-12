package com.unsolved.hgu.problem;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    Page<Problem> findAll(Specification<ProblemDto> spec, Pageable pageable);

    Optional<Problem> findProblemsById(Integer id);
}
