package com.ufu.domain.auth.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import lombok.Getter;

@Getter
public class SignupResponse {
    private final Long userId;

    private final String loginId;

    private final String email;

    private final String nickname;

    public SignupResponse(User user) {
        this.userId = user.getId();
        this.loginId = user.getLoginId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
