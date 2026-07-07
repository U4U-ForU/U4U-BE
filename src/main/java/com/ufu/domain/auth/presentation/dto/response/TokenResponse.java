package com.ufu.domain.auth.presentation.dto.response;

import com.ufu.domain.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "토큰 응답")
public class TokenResponse {
    @Schema(description = "Access Token")
    private final String accessToken;

    @Schema(description = "Refresh Token")
    private final String refreshToken;

    @Schema(description = "사용자 권한", example = "USER")
    private final Role role;
}
