package com.unsolved.hguapis.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/unsolved-hgu/api")
@RestController
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("/userinfo")
    public List<UserInfoDto> list() {
        return this.userInfoService.getAllUserInfos();
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Test!";
    }
}
