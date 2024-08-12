package com.unsolved.hgu.user;

import com.unsolved.hgu.problem.ProblemService;
import jakarta.validation.Valid;
import java.util.Objects;
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
public class UserController {

    private final UserService userService;
    private final ProblemService problemService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        System.out.println(userCreateForm);
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "Passwords do not match", "패스워드가 일치하지 않슴다");
            return "signup_form";
        }

        if (!Objects.equals(userCreateForm.getLoginState(), LoginState.VERIFIED.getState())) {
            LoginState loginState = LoginState.valueOf(userCreateForm.getLoginState());
            bindingResult.rejectValue(loginState.getField(), "state error", loginState.getErrorMessage());
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("signupFailed", "duplicate username", "이미 등록된 사용자 혹은 닉네임입니다.");
            return "signup_form";
        } catch (Exception e) {
            bindingResult.rejectValue("signupFailed", "exception", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
}
