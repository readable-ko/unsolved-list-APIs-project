package com.unsolved.hguapis.question;

import com.unsolved.hguapis.answer.Answer;
import com.unsolved.hguapis.exception.DataNotFoundException;
import com.unsolved.hguapis.siteUser.SiteUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> userOne = root.join("author", JoinType.LEFT);
                Join<Question, Answer> answer = root.join("answers", JoinType.LEFT);
                Join<Question, SiteUser> userTwo = answer.join("author", JoinType.LEFT);

                return cb.or(
                        cb.like(root.get("subject"), "%" + kw + "%"),
                        cb.like(root.get("content"), "%" + kw + "%"),
                        cb.like(userOne.get("username"), "%" + kw + "%"),
                        cb.like(answer.get("content"), "%" + kw + "%"),
                        cb.like(userTwo.get("username"), "%" + kw + "%")
                );
            }
        };
    }

    public Page<Question> getQuestions(int page, String keyword) {
        List<Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createdDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(orders));
        Specification<Question> search = search(keyword);

        return this.questionRepository.findAll(search, pageable);
    }

    public Question getQuestionById(int id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        }
        throw new DataNotFoundException("question not found");
    }

    public void createQuestion(String subject, String content, SiteUser author) {
        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .createdDate(LocalDateTime.now())
                .author(author)
                .build();
        this.questionRepository.save(question);
    }

    public void modifyQuestion(Question question, String subject, String content) {
        Question updatedQuestion = question.toBuilder()
                .subject(subject)
                .content(content)
                .modifiedDate(LocalDateTime.now())
                .build();
        this.questionRepository.save(updatedQuestion);
    }

    public void deleteQuestion(Question question) {
        this.questionRepository.delete(question);
    }

    public void voteQuestion(Question question, SiteUser siteUser) {
        question.voter.add(siteUser);
        this.questionRepository.save(question);
    }
}
