package com.unsolved.hguapis.user;

import com.unsolved.hguapis.exception.DataNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;

    public UserInfo getUserInfo(String username) {
        Optional<UserInfo> userInfo = userInfoRepository.findByUsername(username);
        if (userInfo.isPresent()) {
            return userInfo.get();
        }
        throw new DataNotFoundException("User not found");
    }

    public List<UserInfoDto> getAllUserInfos() {
        List<UserInfoDto> userInfo = userInfoRepository.findAllUserInfoDto(Sort.by(Direction.DESC, "solvedCount"));
        if (userInfo.isEmpty()) {
            throw new DataNotFoundException("User List not found");
        }
        System.out.println(userInfo);
        return userInfo;
    }
}
