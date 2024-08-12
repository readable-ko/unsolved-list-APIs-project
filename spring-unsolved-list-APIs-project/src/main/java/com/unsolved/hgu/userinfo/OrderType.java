package com.unsolved.hgu.userinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum OrderType {
    CONTRIBUTE("solvedCount"),
    CLASS("subClass"),
    RECENT("modifyDate"),
    TIER("tier"),
    COUNT("solvedCount");

    private final String title;
}
