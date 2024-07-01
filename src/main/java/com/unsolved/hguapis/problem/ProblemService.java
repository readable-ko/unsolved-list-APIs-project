package com.unsolved.hguapis.problem;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public Page<ProblemDto> getProblems(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Problem> problems = this.problemRepository.findUnsolvedProblems(pageable);
        return problems.map(ProblemMapper::toDto);
    }
}
