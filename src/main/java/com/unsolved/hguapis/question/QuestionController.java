package com.unsolved.hguapis.question;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("")
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       Model model) {
        Page<Question> paging = this.questionService.getQuestions(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "board";
    }

    @GetMapping(value = "/detail/{id}")
    public String test(@PathVariable Integer id, Model model) {
        Question question = this.questionService.getQuestionById(id);
        model.addAttribute("question", question);
        return "test";
    }
}
