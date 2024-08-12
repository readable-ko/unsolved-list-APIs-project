package com.unsolved.hgu.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsernameForm {
    @NotEmpty(message = "이름을 입력하세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_-]{3,20}$", message = "3글자 이상 입력해주세요.")
    private String username;
}
