package com.unsolved.hgu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("/robots.txt")
    public String index() {
        return "robots.txt";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/unsolved-hgu";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy_policy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}
