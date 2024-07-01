package com.unsolved.hguapis.problem;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/unsolved-hgu/problem")
@Controller
public class ProblemController {
    private final ProblemService problemService;

    @GetMapping("")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<ProblemDto> paging = this.problemService.getProblems(page);
        model.addAttribute("paging", paging);
        return "problem";
    }
}
