package com.unsolved.hguapis.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @NotEmpty(message = "이메일 주소는 필수사항입니다.")
    @Email
    private String email;
    
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자 별명은 필수사항입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수사항입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수사항입니다.")
    private String passwordCheck;
}
