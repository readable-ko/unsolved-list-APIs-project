package com.unsolved.hguapis.userinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/unsolved-hgu")
@Controller
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("")
    public String home() {
        return "home";
    }

    @GetMapping("/ranking")
    public String test(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "types", defaultValue = "CONTRIBUTE") String types, Model model) {
        Page<UserInfoDto> paging = this.userInfoService.getAllUserInfos(page, kw, types);

        model.addAttribute("paging", paging);
        model.addAttribute("types", types);
        model.addAttribute("kw", kw);
        return "ranking";
    }

    @GetMapping("/introduction")
    public String introduction() {
        return "introduce";
    }
}
