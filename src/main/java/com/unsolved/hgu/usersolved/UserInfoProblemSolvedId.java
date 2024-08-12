package com.unsolved.hgu.usersolved;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UserInfoProblemSolvedId implements Serializable {
    @Column
    private String userInfoUsername;

    @Column
    private int problemSolvedId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserInfoProblemSolvedId userInfoProblemSolvedId = (UserInfoProblemSolvedId) obj;
        return Objects.equals(getUserInfoUsername(), userInfoProblemSolvedId.getUserInfoUsername()) &&
                Objects.equals(getProblemSolvedId(), userInfoProblemSolvedId.getProblemSolvedId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInfoUsername, problemSolvedId);
    }
}
