package com.unsolved.hguapis.siteUser;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class SiteUserController {
    private final SiteUserService siteUserService;

    @GetMapping("/signup")
    public String signup(SiteUserCreateForm siteUserCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(SiteUserCreateForm siteUserCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!siteUserCreateForm.getPassword().equals(siteUserCreateForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordInCorrect", "비밀번호 확인과 비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            siteUserService.create(siteUserCreateForm.getUsername(), siteUserCreateForm.getEmail(),
                    siteUserCreateForm.getPassword());
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signUpFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            bindingResult.reject("signUpFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}
