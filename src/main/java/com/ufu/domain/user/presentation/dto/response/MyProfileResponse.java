package com.ufu.domain.user.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import lombok.Getter;

@Getter
public class MyProfileResponse {
    private final String loginId;

    private final String nickname;

    private final int currency;

    public MyProfileResponse(User user) {
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
        this.currency = user.getCurrency();
    }
}
