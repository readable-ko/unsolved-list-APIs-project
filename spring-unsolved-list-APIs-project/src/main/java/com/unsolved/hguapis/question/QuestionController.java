package com.unsolved.hguapis.question;

import com.unsolved.hguapis.answer.AnswerForm;
import com.unsolved.hguapis.siteUser.SiteUser;
import com.unsolved.hguapis.siteUser.SiteUserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createQuestion(QuestionForm questionForm) {
        return "board_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "board_form";
        }

        SiteUser siteUser = this.siteUserService.getSiteUser(principal.getName());
        this.questionService.createQuestion(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/board";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Integer id, QuestionForm questionForm, Principal principal) {
        Question question = this.questionService.getQuestionById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "board_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/modify/{id}")
    public String modifyQuestion(@PathVariable("id") Integer id, @Valid QuestionForm questionForm,
                                 BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "board_form";
        }
        Question question = this.questionService.getQuestionById(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        this.questionService.modifyQuestion(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/board/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestionById(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        this.questionService.deleteQuestion(question);
        return "redirect:/board";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/vote/{id}")
    public String voteQuestion(@PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestionById(id);
        SiteUser siteUser = this.siteUserService.getSiteUser(principal.getName());
        this.questionService.voteQuestion(question, siteUser);
        return String.format("redirect:/board/detail/%s", id);
    }
}
