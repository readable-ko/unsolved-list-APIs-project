package com.unsolved.hgu.problem;

import com.unsolved.hgu.user.SiteUser;
import com.unsolved.hgu.user.UserService;
import java.security.Principal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ProblemController {
    private final ProblemService problemService;
    private final UserService userService;

    @GetMapping("/problem")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "types", defaultValue = "NONE") String types, Principal principal) {
        Page<ProblemDto> paging;
        Set<Integer> userSaved = Set.of();

        //Login case
        if (principal != null) {
            SiteUser siteUser = this.userService.getUser(principal.getName());
            userSaved = this.problemService.getUserSolvedProblemIdsBySiteUser(siteUser);
            paging = this.problemService.getProblemsBySiteUser(page, kw.strip(), types, siteUser);
        } else {
            paging = this.problemService.getProblems(page, kw.strip(), types);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("types", types);
        model.addAttribute("userSaved", userSaved);

        return "problem";
    }
}
