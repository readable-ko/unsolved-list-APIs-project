package com.unsolved.hguapis.question;

import com.unsolved.hguapis.answer.AnswerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String detail(@PathVariable("id") Integer id, Model model, AnswerForm answerForm) {
        Question question = this.questionService.getQuestionById(id);
        model.addAttribute("question", question);
        return "board_detail";
    }

    @GetMapping("/create")
    public String createAnswer(QuestionForm questionForm) {
        return "board_form";
    }

    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "board_form";
        }
//        this.questionService.createQuestion(questionForm.getSubject(), questionForm.getContent());
        return "redirect:/board";
    }
}
