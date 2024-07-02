package com.unsolved.hguapis.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@RequestMapping("/unsolved-hgu")
@Controller
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("/userinfo")
    @ResponseBody
    public List<UserInfoDto> list() {
        return this.userInfoService.getAllUserInfos();
    }

    @GetMapping("")
    public String home() {
        return "home";
    }

    @GetMapping("/test")
    public String test() {
        return "problem";
    }
}
