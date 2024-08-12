package com.unsolved.hgu.user;

import com.unsolved.hgu.answer.Answer;
import com.unsolved.hgu.answer.AnswerRepository;
import com.unsolved.hgu.exception.DataNotFoundException;
import com.unsolved.hgu.problem.Problem;
import com.unsolved.hgu.problem.ProblemRepository;
import com.unsolved.hgu.question.Question;
import com.unsolved.hgu.question.QuestionRepository;
import com.unsolved.hgu.user.oauth.OAuth2UserDetails;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Builder
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ProblemRepository problemRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SiteUser create(String username, String email, String password) {
        SiteUser siteUser = SiteUser.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .provider("site")
                .role(UserRole.USER)
                .build();

        this.userRepository.save(siteUser);
        return siteUser;
    }

    // 유저 닉네임 변경
    public SiteUser modifyUserName(SiteUser siteUser, String username) {
        SiteUser modifiedUser = siteUser.toBuilder()
                .username(username)
                .build();

        this.userRepository.save(modifiedUser);
        reloadLoginStatus(modifiedUser);

        return modifiedUser;
    }

    // 사이트 로그인의 경우
    private void reloadSiteLoginStatus(SiteUser siteUser, Authentication authentication) {
        UserDetails userDetails = new User(
                siteUser.getUsername(),
                siteUser.getPassword(),
                authentication.getAuthorities()
        );
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // 소셜 로그인 사용자의 경우
    private void reloadOAuthLoginStatus(SiteUser siteUser, OAuth2AuthenticationToken authentication) {
        OAuth2UserDetails userDetails = new OAuth2UserDetails(siteUser, authentication.getPrincipal().getAttributes());
        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                userDetails,
                authentication.getAuthorities(),
                authentication.getAuthorizedClientRegistrationId()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    // 유저 닉네임 변경 후 정보 갱신 반영
    private void reloadLoginStatus(SiteUser siteUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            reloadOAuthLoginStatus(siteUser, (OAuth2AuthenticationToken) authentication);
        } else {
            reloadSiteLoginStatus(siteUser, authentication);
        }
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("Site user not found");
        }
    }

    public MyPageUserDto getUserDto(String username, int page) {
        Optional<SiteUser> optionalSiteUser = this.userRepository.findByUsername(username);
        if (optionalSiteUser.isEmpty()) {
            throw new DataNotFoundException("Site user not found");
        }

        SiteUser siteUser = optionalSiteUser.get();

        List<Question> questions = this.questionRepository.findAllByAuthor(siteUser);
        List<Answer> answers = this.answerRepository.findAllByAuthor(siteUser);

        return MyPageUserDto.builder()
                .siteUserDto(SiteUserMapper.toDto(siteUser, getSavedProblems(siteUser, page)))
                .writtenQuestions(questions)
                .writtenAnswers(answers)
                .build();
    }

    public Page<Problem> getSavedProblems(SiteUser siteUser, int page) {
        Pageable pageable = PageRequest.of(page, 4);

        return this.userRepository.findSiteUserSavedProblems(siteUser, pageable);
    }

    public Boolean saveProblem(SiteUser siteuser, Problem problem) {
        boolean add = true;
        if (siteuser.getSavedProblem().contains(problem)) {
            siteuser.getSavedProblem().remove(problem);
            add = false;
        } else {
            siteuser.getSavedProblem().add(problem);
        }

        this.userRepository.save(siteuser);
        return add;
    }

    public Boolean isExistEmail(String email) {
        Optional<SiteUser> siteUser = this.userRepository.findByEmailAndProvider(email, "site");
        return siteUser.isPresent();
    }
}
