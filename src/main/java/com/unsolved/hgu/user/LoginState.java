package com.unsolved.hgu.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginState {
    NOTHING("NOTHING", "email", "이메일 인증이 필요합니다."),
    REQUEST("REQUEST", "verifyCode", "인증 번호 확인이 필요합니다."),
    VERIFIED("VERIFIED", "success", "");

    private final String state;
    private final String field;
    private final String errorMessage;
}
