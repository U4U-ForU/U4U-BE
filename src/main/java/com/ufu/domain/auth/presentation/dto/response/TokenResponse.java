package com.ufu.domain.auth.presentation.dto.response;

import com.ufu.domain.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private final String accessToken;

    private final String refreshToken;

    private final Role role;
}
