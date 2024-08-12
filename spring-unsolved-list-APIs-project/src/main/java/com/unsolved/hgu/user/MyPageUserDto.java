package com.unsolved.hgu.user;

import com.unsolved.hgu.answer.Answer;
import com.unsolved.hgu.question.Question;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class MyPageUserDto {
    private SiteUserDto siteUserDto;
    private List<Question> writtenQuestions;
    private List<Answer> writtenAnswers;
}
