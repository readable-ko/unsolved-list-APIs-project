package com.unsolved.hguapis.problem;

import com.unsolved.hguapis.usersolved.SolvedProblemMapper;
import com.unsolved.hguapis.usersolved.UserInfoProblemSolved;
import com.unsolved.hguapis.usersolved.UserInfoProblemSolvedDto;
import com.unsolved.hguapis.usersolved.UserInfoProblemSolvedService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final UserInfoProblemSolvedService userInfoProblemSolvedService;


    private Specification<ProblemDto> search(String kw, LevelType types) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ProblemDto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<UserInfoProblemSolved> subRoot = subquery.from(UserInfoProblemSolved.class);
                subquery.select(subRoot.get("problem").get("id"));

                List<Predicate> predicates = new ArrayList<>();
                predicates.add(root.get("id").in(subquery).not());

                if (!kw.isBlank()) {
                    Predicate findKeyword = cb.or(
                            cb.like(root.get("id").as(String.class), "%" + kw + "%"),
                            cb.like(root.get("title"), "%" + kw + "%"),
                            cb.like(root.join("tags").get("name"), "%" + kw + "%")
                    );
                    predicates.add(findKeyword);
                }

                if (types.isNotNone()) {
                    int lowerBound = types.getLevel() - 5;
                    int upperBound = types.getLevel();
                    predicates.add(cb.and(
                            cb.greaterThan(root.get("level"), lowerBound),
                            cb.lessThanOrEqualTo(root.get("level"), upperBound)
                    ));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    public Page<ProblemDto> getProblems(int page, String keyword, String types) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Problem> problems;
        LevelType levelType = LevelType.valueOf(types);

        if (levelType.equals(LevelType.FAVORITE)) {
            Page<UserInfoProblemSolvedDto> userInfoProblemSolvedDto = this.userInfoProblemSolvedService.getProblems(
                    keyword, page);
            return userInfoProblemSolvedDto.map(SolvedProblemMapper::solvedToProblem);
        }

        Specification<ProblemDto> spec = search(keyword, levelType);
        problems = this.problemRepository.findAll(spec, pageable);

        return problems.map(ProblemMapper::toDto);
    }
}
