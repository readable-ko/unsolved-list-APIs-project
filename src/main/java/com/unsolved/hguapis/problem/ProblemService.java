package com.unsolved.hguapis.problem;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public List<ProblemDto> getProblems() {
        List<Problem> problems = problemRepository.findAll();
        return problems.stream().map(ProblemMapper::toDto).toList();

    }
}
