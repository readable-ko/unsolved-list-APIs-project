package com.unsolved.hguapis.user;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;

    public Page<UserInfoDto> getAllUserInfos(int page, String kw, String types) {
        OrderType orderType = OrderType.valueOf(types);
        List<Sort.Order> sorts = new ArrayList<>();

        sorts.add(Sort.Order.desc(orderType.getTitle()));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (orderType == OrderType.CONTRIBUTE) {
            Page<UserInfoContributeDto> userInfos = userInfoRepository.findContributeUserInfo(kw, pageable);
            return userInfos.map(UserInfoMapper::ContributeDtoToDto);
        }

        return this.userInfoRepository.findAllUserInfo(kw, pageable);
    }
}
