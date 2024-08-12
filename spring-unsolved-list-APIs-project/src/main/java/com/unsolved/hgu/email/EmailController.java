package com.unsolved.hgu.email;

import com.unsolved.hgu.exception.DataNotFoundException;
import com.unsolved.hgu.user.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/emails/verification-request")
    public Map<String, String> mailSend(@RequestBody @Valid EmailRequestForm emailRequestForm,
                                        BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("error", "[ERROR] 메일 주소를 입력해주세요.");
            return response;
        }

        String email = emailRequestForm.getEmail();
        if (userService.isExistEmail(email)) {
            response.put("error", "[ERROR] 이미 가입된 이메일 주소입니다.");
            return response;
        }

        String code = emailService.joinEmail(email);
        response.put("code", code);

        return response;
    }

    @PostMapping("/emails/verification")
    public String mailVerify(@RequestBody @Valid EmailVerifyForm emailVerifyForm) {
        Boolean valid = emailService.isValidAuthString(emailVerifyForm.getEmail(), emailVerifyForm.getAuthString());
        if (valid) {
            return "[Success] 이메일 인증에 성공했습니다.";
        }
        throw new DataNotFoundException("[Fail] 이메일 인증 번호가 다릅니다!");
    }
}
