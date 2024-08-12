package com.unsolved.hgu;

import com.unsolved.hgu.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HguApplicationTests {
    @Autowired
    private QuestionService questionService;

    @Test
    void contextLoads() {
        for (int i = 0; i < 100; i++) {
            String subject = String.format("테스트 데이터입니다.[%03d]", i);
            String content = "이 땅에 영광 있으리라";
            this.questionService.create(subject, content, null);
        }
    }

}
