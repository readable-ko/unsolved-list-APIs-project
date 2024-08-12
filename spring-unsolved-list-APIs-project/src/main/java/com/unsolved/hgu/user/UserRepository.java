package com.unsolved.hgu.user;

import com.unsolved.hgu.problem.Problem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);

    Optional<SiteUser> findByEmailAndProvider(String email, String provider);

    @Query("SELECT u "
            + "FROM SiteUser u "
            + "WHERE u.provider = :provider and u.providerId = :providerId ")
    Optional<SiteUser> findByLoginId(@Param("provider") String provider, @Param("providerId") String providerId);

    @Query("SELECT p "
            + "FROM SiteUser u JOIN u.savedProblem p "
            + "WHERE u = :siteUser ")
    Page<Problem> findSiteUserSavedProblems(@Param("siteUser") SiteUser siteUser, Pageable pageable);
}
