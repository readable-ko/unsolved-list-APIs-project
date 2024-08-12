package com.unsolved.hgu.problem;

import com.unsolved.hgu.exception.DataNotFoundException;
import com.unsolved.hgu.user.SiteUser;
import com.unsolved.hgu.user.UserRepository;
import com.unsolved.hgu.usersolved.SolvedProblemMapper;
import com.unsolved.hgu.usersolved.UserInfoProblemSolved;
import com.unsolved.hgu.usersolved.UserInfoProblemSolvedService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final UserRepository siteUserRepository;


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

    private Page<ProblemDto> getFavoriteProblems(String keyword, int page) {
        return this.userInfoProblemSolvedService.getProblems(keyword, page)
                .map(SolvedProblemMapper::solvedToProblem);
    }

    private Page<ProblemDto> getSavedProblems(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.siteUserRepository.findSiteUserSavedProblems(siteUser, pageable)
                .map(ProblemMapper::toDto);
    }

    private Page<ProblemDto> getDefaultProblems(String keyword, LevelType levelType, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Specification<ProblemDto> spec = search(keyword, levelType);
        return this.problemRepository.findAll(spec, pageable)
                .map(ProblemMapper::toDto);
    }

    public Page<ProblemDto> getProblems(int page, String keyword, String types, SiteUser siteUser) {
        LevelType levelType = LevelType.valueOf(types);

        if (levelType.equals(LevelType.FAVORITE)) {
            return getFavoriteProblems(keyword, page);
        } else if (levelType.equals(LevelType.SAVED)) {
            return getSavedProblems(siteUser, page);
        } else {
            return getDefaultProblems(keyword, levelType, page);
        }
    }

    public Page<ProblemDto> getProblems(int page, String keyword, String types) {
        return getProblems(page, keyword, types, null);
    }

    public Page<ProblemDto> getProblemsBySiteUser(int page, String keyword, String types, SiteUser siteUser) {
        return getProblems(page, keyword, types, siteUser);
    }

    public Set<Integer> getUserSolvedProblemIdsBySiteUser(SiteUser siteUser) {
        return siteUser.getSavedProblem()
                .stream()
                .map(Problem::getId)
                .collect(Collectors.toSet());
    }

    public Problem getProblemById(int id) {
        Optional<Problem> problem = this.problemRepository.findProblemsById(id);
        if (problem.isPresent()) {
            return problem.get();
        }
        throw new DataNotFoundException("Problem not found");
    }
}
