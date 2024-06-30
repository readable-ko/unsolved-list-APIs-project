package com.unsolved.hguapis.problem;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/unsolved-hgu/problem")
@Controller
public class ProblemController {
    private final ProblemService problemService;

    @GetMapping("")
    public String problems(Model model) {
        List<ProblemDto> problemItems = this.problemService.getProblems();
        List<ProblemDto> problemDto = problemItems.subList(15970, 15990);
        model.addAttribute("problemItems", problemDto);
        return "problem";
    }

    @GetMapping("/table")
    public String problem(Model model) {
        List<ProblemDto> problemItems = this.problemService.getProblems();
        model.addAttribute("problemItems", problemItems);
        return "layout";
    }
}
