package com.unsolved.hgu.email;

import lombok.Data;

@Data
public class EmailVerifyForm {
    private String email;
    private String authString;
}
