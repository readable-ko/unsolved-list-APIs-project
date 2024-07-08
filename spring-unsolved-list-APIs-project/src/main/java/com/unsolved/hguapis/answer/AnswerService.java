package com.unsolved.hguapis.answer;

import com.unsolved.hguapis.exception.DataNotFoundException;
import com.unsolved.hguapis.question.Question;
import com.unsolved.hguapis.user.SiteUser;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer createAnswer(Question question, String content, SiteUser author) {
        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .createdDate(LocalDateTime.now())
                .author(author)
                .build();
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isEmpty()) {
            throw new DataNotFoundException("answer not found");
        }
        return answer.get();
    }

    public void modifyAnswer(Answer answer, String content) {
        answer.updateContent(content);
        this.answerRepository.save(answer);
    }

    public void deleteAnswer(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void voteAnswer(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
