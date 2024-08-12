package com.unsolved.hgu.user;

import com.unsolved.hgu.problem.Problem;
import com.unsolved.hgu.problem.ProblemService;
import com.unsolved.hgu.util.UrlEncoder;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserSelfController {
    private final UserService userService;
    private final ProblemService problemService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save/problem/{id}")
    @ResponseBody
    public Boolean saveProblem(@PathVariable("id") Integer id, Principal principal) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Problem problem = this.problemService.getProblemById(id);

        return this.userService.saveProblem(siteUser, problem);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail/{username}")
    public String detail(@PathVariable("username") String username, Model model, UsernameForm usernameForm,
                         @RequestParam(value = "page", defaultValue = "0") int page) {
        MyPageUserDto myPageUserDto = this.userService.getUserDto(username, page);

        model.addAttribute("user", myPageUserDto.getSiteUserDto());
        model.addAttribute("questions", myPageUserDto.getWrittenQuestions());
        model.addAttribute("answers", myPageUserDto.getWrittenAnswers());

        return "mypage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update/password")
    public String updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Model model) {
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/username")
    @ResponseBody
    public Map<String, String> updateUsername(@Valid UsernameForm usernameForm, Principal principal,
                                              BindingResult bindingResult,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              Model model) {
        MyPageUserDto myPageUserDto = this.userService.getUserDto(principal.getName(), page);

        model.addAttribute("user", myPageUserDto.getSiteUserDto());
        model.addAttribute("questions", myPageUserDto.getWrittenQuestions());
        model.addAttribute("answers", myPageUserDto.getWrittenAnswers());

        SiteUser newUser;
        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                response.put("error", error.toString());
            }
            return response;
        }

        if (myPageUserDto.getSiteUserDto().getUsername().equals(usernameForm.getUsername())) {
            response.put("error", "이미 사용 중인 유저명입니다.");
            return response;
        }

        try {
            SiteUser siteUser = this.userService.getUser(myPageUserDto.getSiteUserDto().getUsername());
            newUser = this.userService.modifyUserName(siteUser, usernameForm.getUsername());
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("username", "username.exists", "Username already exists");
            response.put("error", "이미 존재하는 유저명입니다.");
            return response;
        }

        String encodedUsername = UrlEncoder.encodeURL(newUser.getUsername());

        response.put("success", "/user/detail/" + encodedUsername);
        return response;
    }
}
