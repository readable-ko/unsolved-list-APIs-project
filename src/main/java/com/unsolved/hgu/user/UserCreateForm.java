package com.unsolved.hgu.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;

    @Size(min = 2, max = 25)
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String username;

    @NotEmpty(message = "사용자 비밀번호는 필수 항목입니다.")
    private String password1;

    @NotEmpty(message = "사용자 비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "메일 인증이 필요합니다.")
    private String verifyCode;

    private String loginState = LoginState.NOTHING.getState();
}
