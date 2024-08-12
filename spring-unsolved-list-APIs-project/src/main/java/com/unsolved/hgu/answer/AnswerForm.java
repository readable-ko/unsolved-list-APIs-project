package com.unsolved.hgu.answer;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerForm {
    @NotEmpty(message = "내용을 입력하세요!")
    private String content;
}
